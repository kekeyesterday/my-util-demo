
package com.utilsTemplate.utils;

/**
 * Byte 操作工具类
 * @author 王通晓
 *
 */

public class ByteUtil {
		
		/**
		 * 将Integer 转化为指定长度的Byte数组
		 * @param n 待转化的Integer数值
		 * @param len byte数值长度
		 * @return
		 */
		public static byte[] intToBytes(int n,int len){  
			byte[] b = new byte[len];  
			for(int i = len;i>0;i--){
				b[i-1] = (byte)(n>>(len-i)*8);
			}
			return b;
		}  
	
		/**
		 * 将Byte数组转化为Integer
		 * @param b
		 * @return
		 */
		public static int bytesToInt(byte[] b){
			int len = b.length-1;
			int n = b[len] & 0xff;
			for(int i = 0;i<len;i++){
				n = n+ (b[i] <<((len-i)*8));
			}
			return n;
		}
		
	    /**
	     * 把int类型的app软件版本转换为x.x形式的字符串
	     * @param appVersion
	     * @return
	     */
	    public static String toStringAppVersion(int appVersion) {
	        byte[] bytes = intToBytes(appVersion,2);
	        String majorVersion = "" + (int)bytes[0];
	        String minorVersion = "" + (int)bytes[1];
	        return (majorVersion + '.' + minorVersion);
	    }
		
	    /**
	     * 将Byte数组转换为String
	     * @param b
	     * @param pos
	     * @param offset
	     * @return
	     */
		public static String readBytes2Str(byte[] b,int pos,int offset){
			byte[] msgBytes = new byte[offset];
			System.arraycopy(b, pos, msgBytes, 0, offset);
			return new String(msgBytes);
		}
		
		/**
		 * 将Byte数组转换为Integer
		 * @param b
		 * @param pos
		 * @param offset
		 * @return
		 */
		public static int readBytes2Int(byte[] b,int pos,int offset){
			byte[] valueBytes = new byte[offset];
			System.arraycopy(b, pos, valueBytes, 0, offset);
			return bytesToInt(valueBytes);
		}
		
		
		public  static byte[] hexStringToByte(String hex) {
			char[] data = hex.toCharArray();
			int len = data.length;
			if ((len & 0x01) != 0) {
				throw new RuntimeException("Odd number of characters.");
			}

			byte[] out = new byte[len >> 1];
			// two characters form the hex value.
			for (int i = 0, j = 0; j < len; i++) {
				int f = toDigit(data[j], j) << 4;
				j++;
				f = f | toDigit(data[j], j);
				j++;
				out[i] = (byte) (f & 0xFF);
			}
			return out;
		}
		
		private static  int toDigit(char ch, int index) {
			int digit = Character.digit(ch, 16);
			if (digit == -1) {
				throw new RuntimeException("Illegal hexadecimal character " + ch+ " at index " + index);
			}
			return digit;
		}
		
		public static void main(String[] args) {
			
			int i = 10;
			int t = i<<2;
			System.out.println(t);
			
			t = i>>>2;
			System.out.println(t);
			
		}

}
