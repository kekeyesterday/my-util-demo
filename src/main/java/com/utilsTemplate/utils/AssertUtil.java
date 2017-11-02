

package com.utilsTemplate.utils;

/**
 * 安防服务 断言类
 */
public class AssertUtil {

	private AssertUtil() {
	}

	/**
	 * 验证输入参数必填项
	 *
	 * @param argNames 参数名称，英文逗号分隔
	 * @param args     参数变量
	 * @throws ServLayerException
	 */
	public static void assertRequired(String argNames, Object... args) throws Exception {
		boolean isNull = false;
		String[] argNamesArray = argNames.split(",");
		StringBuilder argName = new StringBuilder("");
		int index = 0;
		for (Object arg : args) {
			if (null == arg) {
				argName.append(argNamesArray[index]);
				isNull = true;
			}
			index++;
		}
		if (isNull) {
			throw new Exception(argName.toString() + " are not allowed to be null");
		}
	}
	
	public static void main(String[] args) {
		try {
			assertRequired("vin",null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
