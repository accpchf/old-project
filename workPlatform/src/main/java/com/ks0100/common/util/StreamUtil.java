package com.ks0100.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 输入流工具类
 * 
 * @author xie linming
 * 
 */
public class StreamUtil {
	/**
	 * 将输入流转换为字节数组
	 * 
	 * @param inStream
	 *            输入流
	 * @return 字节数组
	 * @throws IOException
	 */
	public static byte[] read(InputStream inStream) throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}
}
