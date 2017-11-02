
package com.utilsTemplate.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileUtil {
	private static Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);
	private static List<String> idList = new ArrayList<String>();
	@Value("${opm-pushmsg.file.temp.dir:defaultValue}")
	private String tempPath;
//	private String tempPath = "D:/test";
	private static final Integer TOKEN_LENGTH = 200;
	// private static final Integer VIN_LENGTH = 17;
	private static final String SPLITTED_CHAR = "_";
	public static final String FILE_ANDROID = "android";
	public static final String FILE_IOS = "ios";
	public static final String FILE_YUNOS = "yunos";
	public static String TERMINAL_MIRROR = "mirror";
	public static final String TERMINAL_MIRROR_KEY = "1095";
	
	
	//public static HashMap<Object, Object> fileMap = new HashMap<>();

	/**
	 * 把数据写入本地文件
	 * 
	 * @Title: writeFile
	 * @param param
	 * @return void
	 */
	public  void writeFile(String id, List<String> deviceList, String appKey, String tname) {
		LOGGER.info("=========writeFile开始==========id:{},appKey:{},tname:{},deviceList:{}", id, appKey, tname,
				deviceList);
		MappedByteBuffer byteBuffer = null;
		RandomAccessFile accessFile = null;
		try {
			StringBuilder fileSuffix = new StringBuilder();
			fileSuffix.append(tname).append(SPLITTED_CHAR).append(appKey).append(SPLITTED_CHAR).append(id);

			// 判断当前批次在不在批次列表中
			if (!idList.contains(id)) {
				idList.add(id);
			}
			File fileDir = new File(new StringBuilder(tempPath).append("/msgtemp").toString());
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}

			File file = new File(new StringBuilder(fileDir.getPath()).append("/").append(fileSuffix).toString());
			if (!file.exists()) {
				file.createNewFile();
				file.setExecutable(true);// 设置可执行权限
				file.setReadable(true);// 设置可读权限
				file.setWritable(true);// 设置可写权限
				// 创建读索引文件
				File readIdxfile = new File(
						new StringBuilder(fileDir.getPath()).append("/idx_").append(fileSuffix).toString());
				readIdxfile.createNewFile();
				readIdxfile.setExecutable(true);// 设置可执行权限
				readIdxfile.setReadable(true);// 设置可读权限
				readIdxfile.setWritable(true);// 设置可写权限
			}
			//if(fileMap.containsKey(file.getName())){
			accessFile = new RandomAccessFile(file, "rw");
			//fileMap.put(file.getName(), accessFile);
			accessFile.seek(file.length());
			FileChannel fileChannel = accessFile.getChannel();
			byteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, file.length(),
					TOKEN_LENGTH * deviceList.size());
			for (String deviceToken : deviceList) {
				byteBuffer.put(leftPad(deviceToken).getBytes());
			}
			close(byteBuffer);
			accessFile.close();
		} catch (Exception e) {
			LOGGER.error("write file failed", e);
		} finally {
			close(byteBuffer);
			if (accessFile != null) {
				try {
					accessFile.close();
				} catch (IOException e) {
					LOGGER.error(new StringBuilder("****文件关闭异常**** ==> :").append(e).toString());
				}
			}

		}
		LOGGER.info("=========writeFile结束==========");
	}

	private static String leftPad(String str) {
		int length = str.length();
		int interval = TOKEN_LENGTH - length;
		if (interval > 0) {
			str = StringUtils.leftPad(str, TOKEN_LENGTH, "*");
		}

		return str;
	}

	private static String rmleftPad(String str) {
		str = str.replace("*", "");
		return str;
	}

	public static void main(String[] args) {
//		String test = leftPad("ABCD");
//		System.out.println(test);
//		String test2 = rmleftPad(test);
//		System.out.println(new Date().getTime());
		FileUtil fileUtil = new FileUtil();
		
		fileUtil.deleteFilesByTagId("1137");
	}

	/**
	 * 关闭文件缓存对象
	 * 
	 * @Title: close
	 * @param byteBuffer
	 */
	public static void close(final MappedByteBuffer byteBuffer) {
		if (null == byteBuffer)
			return;
		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			@SuppressWarnings("restriction")
			public Object run() {
				try {
					Method getCleanerMethod = byteBuffer.getClass().getMethod("cleaner");
					getCleanerMethod.setAccessible(true);
					sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod.invoke(byteBuffer);
					if (null != cleaner) {
						cleaner.clean();
					}
				} catch (Exception e) {
					LOGGER.error("close mappedByteBuffer file failed", e);
				}
				return null;
			}
		});
	}

	/**
	 * 读取token集合
	 * 
	 * @Title: pushVoice
	 * @param tbVoiceMain
	 * @param sec
	 * @throws IOException
	 */
	public List<String> readTokenList(String id, String appKey, String tname) {
		LOGGER.info("=========readTokenList开始==========id:{},appKey:{},tname:{}", id, appKey, tname);
		List<String> tokenList = new ArrayList<>();
		File tokenFile = null, idxFile = null;
		RandomAccessFile vinAccessFile = null, idxAccessFileFile = null;
		MappedByteBuffer vinByteBuffer = null, idxByteBuffer = null;
		try {
			StringBuilder fileSuffix = new StringBuilder();
			fileSuffix.append(tname).append(SPLITTED_CHAR).append(appKey).append(SPLITTED_CHAR).append(id);

			// 判断当前批次在不在批次列表中
			if (!idList.contains(id)) {
				idList.add(id);
			}
			File fileDir = new File(new StringBuilder(tempPath).append("/msgtemp").toString());
			/** vinFile start **/
			tokenFile = new File(new StringBuilder(fileDir.getPath()).append("/").append(fileSuffix).toString());
			if (tokenFile.exists()) {
				vinAccessFile = new RandomAccessFile(tokenFile, "rw");
				// 文件长度，字节数
				long fileLength = vinAccessFile.length();
				FileChannel fileChannel = vinAccessFile.getChannel();
				// 文件映射到内存
				vinByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileLength);
				/** vinFile end **/

				/** idxFile start **/
				idxFile = new File(new StringBuilder(fileDir.getPath()).append("/idx_").append(fileSuffix).toString());
				idxAccessFileFile = new RandomAccessFile(idxFile, "rw");
				// 文件长度，字节数
				FileChannel idxFileChannel = idxAccessFileFile.getChannel();
				idxByteBuffer = idxFileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileLength);
				/** idxFile end **/

				byte[] dst = null;
				String vinToken = "", deviceId = "";
				int index = 0, start = 0;
				index = idxByteBuffer.getInt();
				start = index / TOKEN_LENGTH;
				vinByteBuffer.position(index);
				// 循环读取文件，给用户发送红包
				for (int i = start; i < fileLength / TOKEN_LENGTH; i++) {
					try {
						dst = new byte[TOKEN_LENGTH];
						vinByteBuffer.get(dst, 0, TOKEN_LENGTH);
						vinToken = new String(dst, "UTF-8");
						deviceId = getVinTokenArray(vinToken);

						// 当前读取索引写入文件
						idxByteBuffer.putInt(vinByteBuffer.position());
						tokenList.add(deviceId);
					} catch (Exception e) {
						LOGGER.error(new StringBuilder("****读取device文件错误**** ==>id:").append(id).toString(), e);
						// 失败VIN写入队列
					}
				}
				return tokenList;
			}

		} catch (Exception e) {
			LOGGER.error(new StringBuilder("****读取device文件错误**** ==> :").append(e).toString());
		} finally {
			// vinFile

			try {
				if (null != vinAccessFile) {
					vinAccessFile.close();
				}
			} catch (IOException e) {
				LOGGER.error(new StringBuilder("****文件关闭异常**** ==> :").append(e).toString());
			}
			close(vinByteBuffer);
//			deleteFile(tokenFile);

			try {
				if (null != idxAccessFileFile)
					idxAccessFileFile.close();
			} catch (IOException e) {
				LOGGER.error(new StringBuilder("****文件关闭异常**** ==> :").append(e).toString());
			}
			// idxFile
			close(idxByteBuffer);
//			deleteFile(idxFile);
		}
		LOGGER.info("=========readTokenList结束==========");
		return tokenList;

	}

	private static String getVinTokenArray(String token) {
		token = rmleftPad(token);
		return token;
	}

	/**
	 * 删除文件
	 * 
	 * @Title: deleteFile
	 * @param file
	 */
//	private static void deleteFile(File file) {
//		try {
//			if (file != null && file.exists()) {
//				file.delete();
//			}
//
//		} catch (Exception e) {
//			LOGGER.error(new StringBuilder("****文件删除异常**** ==> :").append(e).toString());
//		}
//	}
	
	
	public void deleteFilesByTagId(String tagId){
		File[] files = findFilesByTagId(tagId);
		deleteFile(files);
	}
	
	/**
	 * 根据tagId查找文件
	 * @param tagId
	 * @return
	 */
	private File[] findFilesByTagId(final String tagId){
		File[] files = null;
		try {
			StringBuilder filePath = new StringBuilder(tempPath).append("/msgtemp");
			File fileDir = new File(filePath.toString());
			if(fileDir.isDirectory()){
				FilenameFilter fileter = new FilenameFilter(){
					@Override
					public boolean accept(File dir, String name) {
						if(name.contains(tagId))
							return true;
						return false;
					}
					
				};
				files = fileDir.listFiles(fileter);
			}
		} catch (Exception e) {
			LOGGER.error("文件过滤异常",e);
			throw e;
		}
		
		return files;
	}

//	public  void deleteFileById(String id, String tname, String appKey) {
//		StringBuilder fileSuffix = new StringBuilder();
//		fileSuffix.append(tname).append(SPLITTED_CHAR).append(appKey).append(SPLITTED_CHAR).append(id);
//		File vinFile = new File(new StringBuilder(tempPath).append("/msgtemp/").append(fileSuffix).toString());
//		deleteFile(vinFile);
//		File idxFile = new File(new StringBuilder(tempPath).append("/msgtemp/idx_").append(fileSuffix).toString());
//		deleteFile(idxFile);
//	}
	
	public void deleteFile(File[] files) {
		if(null != files){
			for(File file:files){
				if(file != null && file.exists()){
					LOGGER.info("delFile:{}",file.getName());
					file.delete();
				}
			}
		}
	}
}
