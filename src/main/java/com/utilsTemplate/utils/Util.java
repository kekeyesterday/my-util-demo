

package com.utilsTemplate.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

public class Util {

	public static String genUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	public static String loadScript(Class<?> cl,String name) {

		String scriptContent = null;
		String fileUrl = cl.getResource("/") + name;
		fileUrl = fileUrl.substring(5);
		System.out.println("--"+fileUrl);
		
		File sqlFile = new File(fileUrl);
		if (sqlFile.exists()) {
			BufferedInputStream bis = null;
			try {
				bis = new BufferedInputStream(new FileInputStream(sqlFile));
				int total = bis.available();

				byte[] scripts = new byte[total];
				bis.read(scripts);

				scriptContent = new String(scripts);
			} catch (Throwable e) {
				e.printStackTrace();
			} finally {
				if (bis != null) {
					try {
						bis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return scriptContent;
	}

}
