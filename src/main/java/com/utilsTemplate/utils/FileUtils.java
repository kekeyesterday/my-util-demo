

package com.utilsTemplate.utils;

import com.saicmotor.telematics.framework.core.exception.ServLayerException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {

	private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

	/**
	 * About File Name
	 */
	public final static String PREX = "TBOX";
	public final static String CTL_TYPE = ".CTLA";// .CTL作为EP11的控制文件，.CTLA作为550的控制文件
	public final static String DAT_TYPE = ".DAT";
	public final static String TMP_TYPE = ".TMP";

	public final static int TMP_FILE_NAME_LEN = 26;
	public final static int CTL_FILE_NAME_LEN = 22;
	public final static int DAT_FILE_NAME_LEN = 22;
	
	private FileUtils() {
	}

	/**
	 * 以当前系统时间生成文件名
	 * 
	 * @return
	 */
	public static String getDateSegment() {
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
		String str = "";
		try {
			str = sdFormat.format(new Date());
		} catch (Exception e) {
			return "";
		}
		if (str.equals("1900-01-01")) {
			str = "";
		}
		return str;
	}

	/**
	 * 根据控制文件名，获取数据文件名
	 * 
	 * @param ctlFileName
	 * @return
	 */
	public static String getDataFileNameByCtlFileName(String ctlFileName) {
		String ret = null;
		if (ctlFileName.indexOf(FileUtils.CTL_TYPE) > 0) {
			ret = ctlFileName.replaceAll(FileUtils.CTL_TYPE, FileUtils.DAT_TYPE);
		} else {
			logger.error("控制文件名" + ctlFileName + "格式不正确.");
		}
		return ret;
	}

	/**
	 * 
	 * 根据InputStream，返回文件中文本内容
	 * 
	 * @param ins
	 * @return 字符串 加行符
	 */
	public static String readInputStream(InputStream ins) {
		StringBuffer result = new StringBuffer();
		if (ins == null) {
			return result.toString();
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
			String inLine = reader.readLine();
			while (inLine != null) {
				result.append(inLine + System.getProperty("line.separator"));
				inLine = reader.readLine();
			}
			reader.close();
			ins.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("读取失败!");
		}
		return result.toString();
	}

	/**
	 * 
	 * 根据文本内容，返回字符串行数组
	 * 
	 * @param content
	 * @return 字符串数组
	 */
	public static String[] readTextForLines(String content) {
		String[] lines = new String[] {};
		if (content.trim().length() > 0) {
			lines = content.split(System.getProperty("line.separator"));
		}
		return lines;
	}

	/**
	 * 读取Excel文件返回Workbook
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static Workbook readExcelFile(MultipartFile file) throws ServLayerException {
		InputStream is = null;
		Workbook wb = null;
		try {
			is = file.getInputStream();
			// 判断文件类型
			String fileName = file.getOriginalFilename();
			wb = readInputStream(is, fileName);
		} catch (IOException e) {
			throw new ServLayerException("readFileFailed", "文件解析失败");
		}
		return wb;
	}
	
	public static Workbook readInputStream(InputStream ins,String fileName) throws ServLayerException {
		Workbook wb = null;
		try {
			if (fileName.endsWith(".xls")) {
				wb = new HSSFWorkbook(ins);
			} else if (fileName.endsWith(".xlsx")) {
				wb = new XSSFWorkbook(ins);
			} else {
				logger.error("读取失败!");
				throw new ServLayerException("errorFileType", "导入失败，请选择正确的导入文件");
			}
		} catch (IOException e) {
			throw new ServLayerException("readFileFailed", "文件解析失败");
		}
		return wb;
	}
	
	public static String getTboxModelFromFileName(String name){
		return name.substring(name.indexOf("-")+1, name.lastIndexOf("-"));
	}
}
