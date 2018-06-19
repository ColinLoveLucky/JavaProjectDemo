/**
 * 
 */
package com.qf.cobra.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * @author LongjunLu
 *
 */
public class ZipUtils {

	/**
	 * GZip解压
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static byte[] uncompress(byte[] input) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(input);
		GZIPInputStream gunzip = new GZIPInputStream(in);
		try {
			byte[] buffer = new byte[256];
			int n;
			while ((n = gunzip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
		} finally {
			gunzip.close();
		}
		in.close();
		out.close();
		return out.toByteArray();
    }
}
