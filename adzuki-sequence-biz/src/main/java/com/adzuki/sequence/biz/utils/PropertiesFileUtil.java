package com.adzuki.sequence.biz.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public final class PropertiesFileUtil {
	
	private static Logger logger = LoggerFactory.getLogger("PropertiesFileUtil");

	private static ClassLoader classLoader = PropertiesFileUtil.class.getClassLoader();

	/**
	 * 基础配置文件
	 */
	public static Properties project_config = readPropertiesFile();
	

	/**
	 * 在class路径下读取文件
	 * 
	 * @param clazzPathFile
	 * @return
	 */
	public static Properties readPropertiesFile() {
		Properties properties = new Properties();
		InputStream is = classLoader.getResourceAsStream("application.properties");
		try {
			properties.load(is);
			String active = (String) properties.get("spring.profiles.active");
			String file = "application";
	        if (active == null || active.isEmpty()) {
	            file = file + ".properties";
	        } else {
	            file = file + "-" + active + ".properties";
	        }
	        properties.load(classLoader.getResourceAsStream(file));
		} catch (Exception cause) {
			logger.error("加载文件发生异常", cause);
			new RuntimeException("加载文件发生异常,", cause);
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException cause) {
					logger.error("文件关闭异常,", cause);
				}
		}
		return properties;
	}
}
