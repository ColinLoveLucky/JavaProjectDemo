package com.quark.cobra.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.quark.cobra.enums.SexEnums;

import lombok.extern.slf4j.Slf4j;

/**
 * 身份证工具类
 * 
 * @author: XianjiCai
 * @date: 2018/02/06 13:11
 */
@Slf4j
public class IDCardUtil {

	private static final String CHINESE_ID_CARD_18_PATTERN = "^\\d{17}[\\dxX]$"; // RRRRRRYYYYMMDDCCCV
	static final Pattern[] CHINESE_ID_CARD_18_RE = new Pattern[] { Pattern.compile(CHINESE_ID_CARD_18_PATTERN) };

	/**
	 * 判断身份证号是否有效
	 * 
	 * @param idCard
	 * @return
	 */
	public static boolean validateIDCard(String idCard) {
		if (StringUtils.isEmpty(idCard)) {
			return false;
		}

		if (!(idCard.length() == 18 // Validate length
				&& validIDCard18(idCard))) { // Validate if it's a valid 18 digit ID
			// card
			return false;
		}
		if (!validIDCardDOB(idCard)) {
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		String idCard = "410927199304294017";
		boolean validateIDCard = validateIDCard(idCard);
		System.out.println(validateIDCard);
	}
	
	/**
	 * 根据身份证号,验证年龄是否有效
	 * 
	 * @param idCard
	 * @param age
	 * @return
	 */
	public static boolean validAgeByIdCard(String idCard, int age) {
		try {
			String birth = extractBirthFromIDCard(idCard);
			
			Date birthDate = DateUtil.parseDateStr(birth, DateUtil.DF_YMD);
			int diffYear = DateUtil.calcDiffYear(birthDate, new Date());
			if(diffYear != age) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 从身份证号提取出生日期
	 * 
	 * @param idCard
	 * @return
	 */
	public static String extractBirthFromIDCard(String idCard) {
		if(StringUtils.isNotBlank(idCard) && idCard.length() == 18) {
			return idCard.substring(6, 14);
		}
		return null;
	}

	/**
	 * 从身份证号中提取性别
	 * 
	 * @param idCard
	 * @return
	 */
	public static SexEnums extractSexFromIDCard(String idCard) {
		if (StringUtils.isNotBlank(idCard)) {
			int beforeLastDigit = Integer.parseInt(idCard.substring(idCard.length() - 2, idCard.length() - 1));
			// If digit in position 17th is odd, gender is male. Otherwise, it's
			// female
			if (beforeLastDigit % 2 == 0) {
				return SexEnums.genderF;
			} else {
				return SexEnums.genderM;
			}
		}
		return null;
	}

	/**
	 * Validates a 18 digits Chinese ID card
	 *
	 * @param idCard
	 * @return
	 */
	public static boolean validIDCard18(String idCard) {

		Matcher matcher = CHINESE_ID_CARD_18_RE[0].matcher(idCard);
		if (matcher.matches()) {
			int sum = 0;
			for (int i = 0; i < 17; i++) {
				sum += idCard18Weight(Integer.parseInt(String.valueOf(idCard.charAt(i))), 18 - i);
			}
			String c0Str = String.valueOf(idCard.charAt(17));
//			int c0 = -1;
//			try {
//				c0 = Integer.parseInt(c0Str);
//			} catch (NumberFormatException e) {
//				log.error("failed to parse int : {}, error: {}", c0Str, e);
//			}
			long c1 = idCard18ModCmpl(sum, 11, 1);
			return "x".equalsIgnoreCase(c0Str) && c1 == 10 || Integer.parseInt(c0Str) - c1 == 0;
		}
		return false;
	}
	
	private static long idCard18ModCmpl(int m, int i, int n) {
		return (i + n - m % i) % i;
	}

	private static long idCard18Weight(int v, int i) {
		return v * (Math.round(Math.pow(2, i - 1)) % 11);
	}

	private static boolean validIDCardDOB(String idCard) {
		return extractDOBFromIDCard(idCard) != null;
	}

	private static Date extractDOBFromIDCard(String idCard) {

		if (StringUtils.isEmpty(idCard)) {
			return null;
		}

		String y = "";
		String m = "";
		String d = "";

		if (idCard.length() == 18) {
			y = idCard.substring(6, 10);
			m = idCard.substring(10, 12);
			d = idCard.substring(12, 14);
		} else if (idCard.length() == 15) {
			y = "19" + idCard.substring(6, 8);
			m = idCard.substring(8, 10);
			d = idCard.substring(10, 12);
		}
		String dateString = y + "-" + m + "-" + d;
		try {
			// Attempt to parse date
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			df.setLenient(false);
			return df.parse(dateString);
		} catch (ParseException e) {
			log.error("failed to parse {} to date, error : {}", dateString, e);
		}
		return null;
	}
}
