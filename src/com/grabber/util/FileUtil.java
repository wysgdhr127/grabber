package com.grabber.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.grabber.constant.CommonConstant;

public class FileUtil {
	
	public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	public static Random random = new Random();
	public static final int RANDOM_NUM = 10000;
	
	public static boolean checkFolder(String folderPath) {

		File file = new File(folderPath);
		
		if (!file.exists()) {
			LoggerUtil.debugLog.debug("Folder doesn't exists, create new folder [path = " + file + "]");
			return file.mkdirs();
		}
		
		return true;
	}
	
	public static boolean deleteFile(String filePath) {

		boolean result = true;
		File file = new File(filePath);
		
		if (!file.exists()) {
			LoggerUtil.debugLog.debug("File doesn't exists, doesn't need delete [path = " + filePath + "]");
		} else {
			if (file.isFile()) {
				result = file.delete();
				LoggerUtil.debugLog.debug("The  filePath is a file ,delete now! [path = " + filePath + "][result = " + result + "]");
			} else {
				File[] files = file.listFiles();
				for (File ele : files) {
					if (ele.isFile()) {
						result = ele.delete() && result;
					} else {
						result = deleteFile(ele.getAbsolutePath()) && result;
					}
				}
				result = file.delete() && result;
				LoggerUtil.debugLog.debug("The  filePath is a Folder ,delete now! [path = " + filePath + "][result = " + result + "]");
			}
		}
		
		return result;
	}
	
	public static String readFile(String filePath, String encoding) {

		if (encoding == null) {
			encoding = CommonConstant.ENCODING_DEFAULT;
		}
		
		StringBuffer sb = new StringBuffer();
		try {
			File file = new File(filePath);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = bufferedReader.readLine();
				if(lineTxt != null){
					sb.append(lineTxt);
				}
				
				while ((lineTxt = bufferedReader.readLine()) != null) {
					sb.append("\n");
					sb.append(lineTxt);
				}
				read.close();
			} else {
				LoggerUtil.debugLog.debug("Can't find the file ! [path = " + filePath + "]");
			}
		} catch (Exception e) {
			LoggerUtil.errorLog.error("Error happen when read the file ! [path = " + filePath + "]", e);
		}
		
		return sb.toString();
		
	}
	
	public static boolean writeTxtFile(String filePath, String content, String encoding) {

		boolean flag = true;
		
		if (encoding == null) {
			encoding = CommonConstant.ENCODING_DEFAULT;
		}
		
		File file = new File(filePath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (!file.exists()) {
			try {
				flag = file.createNewFile();
			} catch (IOException e) {
				LoggerUtil.errorLog.error("Error happen when create the file ! [path = " + filePath + "]", e);
			}
		}
		
		if (!flag) {
			LoggerUtil.debugLog.error("Error happen when create the file ! [path = " + filePath + "]");
		} else {
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(file);
				fileOutputStream.write(content.getBytes(encoding));
				fileOutputStream.close();
			} catch (Exception e) {
				LoggerUtil.errorLog.error("Error happen when write the file ! [path = " + filePath + "]", e);
				flag = false;
			} finally {
				if (fileOutputStream != null) {
					try {
						fileOutputStream.close();
					} catch (IOException e) {
						LoggerUtil.errorLog.error("Error happen when close the file ! [path = " + filePath + "]", e);
					}
				}
			}
		}
		return flag;
	}
	
	public static String getRandomFileName() {

		return DEFAULT_DATE_FORMAT.format(new Date()) + getRandomNumber();
	}
	
	public static String getRandomFileName(String ext) {

		return getRandomFileName() + ext;
	}
	
	public static String getRandomNumber() {

		int num = RANDOM_NUM + random.nextInt(RANDOM_NUM);
		return num + "";
	}
	
	public static void main(String[] args) {

		System.out.println(FileUtil.readFile("conf/entrance/ehaoyao_entrance.conf","utf-8"));
	}
	
}
