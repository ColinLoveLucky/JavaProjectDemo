package com.qf.cobra.util;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

public class FileUtils {

	/**
	 * @Package com.quark.cobra.bizapp.util
	 * @Description 获得指字节流的数组
	 * @author HongguangHu
	 * @param ins
	 * @return
	 * @throws Exception
	 * @since 2017年4月18日 上午11:34:19
	 */
	public static byte[] getBytes(InputStream ins) throws Exception {
		byte[] buffer = null;
		try {
			BufferedInputStream fis = new BufferedInputStream(ins);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(4096);
			byte[] buf = new byte[4096];
			int len;
			while ((len = fis.read(buf)) != -1) {
				bos.write(buf, 0, len);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (Exception e) {
			throw e;
		}
		return buffer;
	}
	/**
	 * 获得指定文件的byte数组
	 * 
	 * @throws Exception
	 */
	public static byte[] getBytes(String filePath) throws Exception {
		byte[] buffer = null;
		try {
			BufferedInputStream fis = new BufferedInputStream(new FileInputStream(filePath));
			ByteArrayOutputStream bos = new ByteArrayOutputStream(4096);
			byte[] buf = new byte[4096];
			int len;
			while ((len = fis.read(buf)) != -1) {
				bos.write(buf, 0, len);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (Exception e) {
			throw e;
		}
		return buffer;
	}

	/**
	 * 获得指定jpg png文件的byte数组
	 * 
	 * @throws Exception
	 */
	public static byte[] getOptBytes(String filePath, String extName) {
		byte[] buffer = null;
		try {
			BufferedImage sourceImg = ImageIO.read(new FileInputStream(filePath));
			ByteArrayOutputStream bos = new ByteArrayOutputStream(4096);
			if ("jpg".equals(extName)) {
				ImageIO.write(sourceImg, "jpeg", bos);
			}
			if ("png".equals(extName)) {
				ImageIO.write(sourceImg, "gif", bos);
			}
			bos.close();
			buffer = bos.toByteArray();
		} catch (Exception e) {
			return buffer;
		}
		return buffer;
	}

	/**
	 * 根据byte数组，生成文件
	 * 
	 * @throws Exception
	 */
	public static void write2File(byte[] bfile, File file) throws Exception {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			throw e;
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					throw e1;
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					throw e1;
				}
			}
		}
	}

	/**
	 * 根据byte数组，生成文件
	 * 
	 * @throws Exception
	 */
	public static long write3File(byte[] imgBytes, File storeFile, String extName) {
		long result = -1;
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(imgBytes);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(storeFile));
			BufferedImage image = ImageIO.read(in);
			if ("jpg".equals(extName)) {
				ImageIO.write(image, "jpeg", out);
			}
			if ("png".equals(extName)) {
				ImageIO.write(image, "gif", out);
			}
			bos.write(out.toByteArray());
			result = out.toByteArray().length;
			bos.close();
			out.close();
			in.close();
			return result;
		} catch (Exception e) {
			return result;
		}
	}

	/**
	 * 转换文件大小
	 */
	public static String FormetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "GB";
		}
		return fileSizeString;
	}
}
