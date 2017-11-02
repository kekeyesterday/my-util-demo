

package com.utilsTemplate.utils;

import java.util.Random;

/**
 *
 */
public class StringSqueUtil {
	private static Random strGen = new Random();;
	private static Random numGen = new Random();;
	private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")
			.toCharArray();;
	private static char[] numbers = ("0123456789").toCharArray();;

	/** * 产生随机字符串 * */
	public static final String randomString(int length) {
		if (length < 1) {
			return null;
		}
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[strGen.nextInt(61)];
		}
		return new String(randBuffer);
	}

	/** * 产生随机数值字符串 * */
	public static final String randomNumStr(int length) {
		if (length < 1) {
			return null;
		}
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbers[numGen.nextInt(9)];
		}
		return new String(randBuffer);
	}

	public static void main(String[] agus) {
		System.out.println(randomNumStr(11));
		String tt = randomString(10);
		System.out.println(tt);

	}
}
