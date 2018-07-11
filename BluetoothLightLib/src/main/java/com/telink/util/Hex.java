package com.telink.util;

/**
 * reference apache commons <a
 * href="http://commons.apache.org/codec/">http://commons.apache.org/codec/</a>
 *
 * @author Aub
 */
public class Hex {
	/**
	 * 用于建立十六进制字符的输出的小写字符数组
	 */
	private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	/**
	 * 用于建立十六进制字符的输出的大写字符数组
	 */
	private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * 将字节数组转换为十六进制字符数组
	 *
	 * @param data byte[]
	 * @return 十六进制char[]
	 */
	public static char[] encodeHex(byte[] data) {
		return encodeHex(data, true);
	}

	/**
	 * 将字节数组转换为十六进制字符数组
	 *
	 * @param data        byte[]
	 * @param toLowerCase <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
	 * @return 十六进制char[]
	 */
	public static char[] encodeHex(byte[] data, boolean toLowerCase) {
		return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	}

	/**
	 * 将字节数组转换为十六进制字符数组
	 *
	 * @param data     byte[]
	 * @param toDigits 用于控制输出的char[]
	 * @return 十六进制char[]
	 */
	protected static char[] encodeHex(byte[] data, char[] toDigits) {
		int    l   = data.length;
		char[] out = new char[ l << 1 ];
		// two characters form the hex value.
		for (int i = 0, j = 0 ; i < l ; i++) {
			out[ j++ ] = toDigits[ (0xF0 & data[ i ]) >>> 4 ];
			out[ j++ ] = toDigits[ 0x0F & data[ i ] ];
		}
		return out;
	}

	/**
	 * 将字节数组转换为十六进制字符串
	 *
	 * @param data byte[]
	 * @param len  int
	 * @return 十六进制String
	 */
	public static String encodeHexStr(byte[] data, int len) {
		if (data != null) {
			if (len > data.length)
				len = data.length;

			if (len > 0) {
				char[] toDigits = DIGITS_UPPER;
				char[] out      = new char[ len << 1 ];
				for (int i = 0, j = 0 ; i < len ; i++) {
					out[ j++ ] = toDigits[ (0xF0 & data[ i ]) >>> 4 ];
					out[ j++ ] = toDigits[ 0x0F & data[ i ] ];
				}

				return new String(out);
			}
		}
		return new String("");
	}

	/**
	 * 将字节数组转换为十六进制字符串
	 *
	 * @param data   byte[]
	 * @param offset int
	 * @param len    int
	 * @return 十六进制String
	 */
	public static String encodeHexStr(byte[] data, int offset, int len) {
		if (data != null) {
			if (len > data.length - offset)
				len = data.length - offset;

			if (len > 0) {
				char[] toDigits = DIGITS_UPPER;
				char[] out      = new char[ len << 1 ];
				for (int i = 0, j = 0 ; i < len ; i++) {
					out[ j++ ] = toDigits[ (0xF0 & data[ i + offset ]) >>> 4 ];
					out[ j++ ] = toDigits[ 0x0F & data[ i + offset ] ];
				}

				return new String(out);
			}
		}
		return new String("");
	}

	/**
	 * 将字节数组转换为十六进制字符串
	 *
	 * @param data byte[]
	 * @return 十六进制String
	 */
	public static String encodeHexStr(byte[] data) {
		return encodeHexStr(data, true);
	}

	/**
	 * 将字节数组转换为十六进制字符串
	 *
	 * @param data        byte[]
	 * @param toLowerCase <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
	 * @return 十六进制String
	 */
	public static String encodeHexStr(byte[] data, boolean toLowerCase) {
		return encodeHexStr(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	}

	/**
	 * 将字节数组转换为十六进制字符串
	 *
	 * @param data     byte[]
	 * @param toDigits 用于控制输出的char[]
	 * @return 十六进制String
	 */
	protected static String encodeHexStr(byte[] data, char[] toDigits) {
		return new String(encodeHex(data, toDigits));
	}

	/**
	 * 将十六进制字符数组转换为字节数组
	 *
	 * @param data 十六进制char[]
	 * @return byte[]
	 * @throws RuntimeException 如果源十六进制字符数组是一个奇怪的长度，将抛出运行时异常
	 */
	public static byte[] decodeHex(char[] data) {
		int len = data.length;
		if ((len & 0x01) != 0) {
			throw new RuntimeException("Odd number of characters.");
		}
		byte[] out = new byte[ len >> 1 ];
		// two characters form the hex value.
		for (int i = 0, j = 0 ; j < len ; i++) {
			int f = toDigit(data[ j ], j) << 4;
			j++;
			f = f | toDigit(data[ j ], j);
			j++;
			out[ i ] = (byte) (f & 0xFF);
		}
		return out;
	}

	/**
	 * 将十六进制字符转换成一个整数
	 *
	 * @param ch    十六进制char
	 * @param index 十六进制字符在字符数组中的位置
	 * @return 一个整数
	 * @throws RuntimeException 当ch不是一个合法的十六进制字符时，抛出运行时异常
	 */
	protected static int toDigit(char ch, int index) {
		int digit = Character.digit(ch, 16);
		if (digit == -1) {
			throw new RuntimeException("Illegal hexadecimal character " + ch
					+ " at index " + index);
		}
		return digit;
	}

	public static void main(String[] args) {
		String srcStr    = "待转换字符串";
		String encodeStr = encodeHexStr(srcStr.getBytes());
		String decodeStr = new String(decodeHex(encodeStr.toCharArray()));
		System.out.println("转换前：" + srcStr);
		System.out.println("转换后：" + encodeStr);
		System.out.println("还原后：" + decodeStr);
	}

	public static byte[] strToBytes(String info, int length) {
		byte[] result    = new byte[ length ];
		char[] chs       = info.toCharArray();
		byte[] utf8Bytes = new byte[ chs.length ];
		for (int i = 0 ; i < chs.length ; i++) {
			if (String.valueOf(chs[ i ]).equalsIgnoreCase("A")) {
				utf8Bytes[ i ] = 10;
			} else if (String.valueOf(chs[ i ]).equalsIgnoreCase("B")) {
				utf8Bytes[ i ] = 11;
			} else if (String.valueOf(chs[ i ]).equalsIgnoreCase("C")) {
				utf8Bytes[ i ] = 12;
			} else if (String.valueOf(chs[ i ]).equalsIgnoreCase("D")) {
				utf8Bytes[ i ] = 13;
			} else if (String.valueOf(chs[ i ]).equalsIgnoreCase("E")) {
				utf8Bytes[ i ] = 14;
			} else if (String.valueOf(chs[ i ]).equalsIgnoreCase("F")) {
				utf8Bytes[ i ] = 15;
			} else {
				utf8Bytes[ i ] = Byte.parseByte(String.valueOf(chs[ i ]));
			}
		}
		for (int x = 0 ; x < length ; x++) {
			result[ x ] = (byte) ((byte) ((utf8Bytes[ 2 * x ]) * 16) + (utf8Bytes[ 2 * x + 1 ]));
		}
		return result;
	}

}
