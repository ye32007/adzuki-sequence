package com.adzuki.sequence.biz.utils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 初始化系统参数和属性功能类
 */
public class InitSystemEvnAndProp {
	
	private static Logger logger = LoggerFactory.getLogger(InitSystemEvnAndProp.class);
	
	//当前系统的环境变量、属性参数、ip地址
	public static Map<String, String> systemEvnAndProp = new HashMap<String, String>();
	//当前实例的实例名
	public static String clusterName=null;
	static {
		logger.info("***********************获取操作系统环境变量,中间件属性参数,IP地址***********************");
		// 获取系统环境变量
		Map<String, String> systemEnv = System.getenv();
		for (String key : systemEnv.keySet()) {
//			logger.info("系统环境变量:" + key + "=" + systemEnv.get(key));
		}
		// 将系统环境变量放到总的Map中
		systemEvnAndProp.putAll(systemEnv);
		
		//将IP地址放到总的Map中
		String ipAddress=NetWorkUtil.ipAddress;
		logger.info("系统属性参数:" + "ipAddress" + "=" + ipAddress);
		systemEvnAndProp.put("ipAddress", ipAddress);
		
		String macAddress=NetWorkUtil.macAddress;
		logger.info("系统属性参数:" + "macAddress" + "=" + macAddress);
		systemEvnAndProp.put("macAddress", macAddress);

		// 获取系统和中间件的属性参数
		Properties systemProperties = System.getProperties();
		for (Object keyObject : systemProperties.keySet()) {
			String key = keyObject.toString();
			String value = systemProperties.getProperty(key);
//			logger.info("系统属性参数:" + key + "=" + value);
			// 将系统和中间件的属性参数放在总的Map中
			systemEvnAndProp.put(key, value);
		}
		


		/**
		 * 从环境变量和属性参数中获取一个在同一台机器中具有唯一性的标识,最好是每个实例的路径，
		 * 记录当前实例的实例号的文件将会放在此目录的cluster/md5序列号.cid中。
		 * 当前为tomcat的catalina.base目录的路径
		 * 如:catalina.base=/export/Domains/xx.local/server1
		 * 则记录当前实例所分配实例号的文件路径为/export/Domains/xx.local/server1/cluster/md5序列号.cid
		 */
		String catalinaBase = systemEvnAndProp.get("catalina.base");
		logger.info("获取本实例的路径为:" + catalinaBase);
		//采用MD5获取文件名的原因是害怕如果对程序文件进行跨机器拷贝，或同机器
		String fileName = SecurityUtils.getMD5(macAddress+ipAddress+catalinaBase);
		logger.info("根据本实例的ip地址:"+ipAddress+"和本实例路径:" + catalinaBase + "计算出记录实例名的文件名字为:" + fileName+".cid");
		
		
		//先判断文件夹是否存在
		File clusterDirFile=new File(catalinaBase+"/cluster");
		if(clusterDirFile.exists())
		{
			//当文件夹存在时
			logger.info("文件夹:"+clusterDirFile.getAbsolutePath()+"已经存在");
		}
		else
		{
			/*
			 * 	当文件夹不存在时,创建cluster目录:
			 * 	当创建成功时返回true
			 * 	当已经存在或创建失败时返回false,如果已存在不对原有目录做任何影响，如果不存在但是创建不成功需要查看目录的读写权限。
			 * 	
			 */
			boolean isClusterDirCreated=clusterDirFile.getAbsoluteFile().mkdirs();
			if(isClusterDirCreated)
			{
				logger.info("文件夹:"+clusterDirFile.getAbsolutePath()+"创建成功程序,进入初始化状态"+isClusterDirCreated);
			}
			else
			{
				String errorMessage="文件夹:"+clusterDirFile.getAbsolutePath()+"创建失败!请检查是否有路径的读写权限!";
				logger.error(errorMessage);
//				throw new RuntimeException(errorMessage);
			}
		}
		
		File clusterNameFile = null;
		try {
		// 先判断实例名存储文件是否存在
		clusterNameFile = new File(catalinaBase + "/cluster/" + fileName + ".cid");
		if (clusterNameFile.exists()) {
			// 当实例名文件存在时
			logger.info("实例名存储文件:" + clusterNameFile.getAbsolutePath() + "已经存在");
		} else {
			logger.info("实例名存储文件:" + clusterNameFile.getAbsolutePath() + "不存在,开始初始化创建.");
			clusterNameFile.getAbsoluteFile().createNewFile();
		}
		
		
		
			/*
			 * 实例名存储文件：
			 * 	当文件存在时不受任何影响文件内容也不会有任何修改
			 * 	当文件不存在时则创建
			 */
			String currentClusterName = FileUtils.readFileToString(clusterNameFile.getAbsoluteFile());
			if(currentClusterName!=null)
				currentClusterName = currentClusterName.trim();
			
			
			if(currentClusterName.length()<=0)
			{
				
				//通过数据库控制获取机器实例号
				int clusterNameInt= JdbcUtil.getClusterName(fileName, ipAddress, macAddress, catalinaBase);
				if(99<clusterNameInt ||0>=clusterNameInt)
				{
					String errorMessage="当前实例名为:"+clusterNameInt+",实例数已超过99个,为保证生成的序列位数不超长,不允许增加实例!";
					logger.error(errorMessage);
					throw new RuntimeException(errorMessage);
				}
				//将数字格式化成2位字符，不满足两位则在前面补0
				currentClusterName= NumberUtil.format(clusterNameInt, 2);
				FileUtils.writeByteArrayToFile(clusterNameFile.getAbsoluteFile(), currentClusterName.getBytes());
				
				
				//写入成功则给static变量供使用
				clusterName=currentClusterName;
				logger.info("为当前实例自动分配的实例号为:"+clusterName);

				
			}
			else{
				clusterName=currentClusterName;
				logger.info("从实例名存储文件中获取到当前实例的UUID为:"+fileName+",实例名为:"+currentClusterName);
					
			}
			
		} 
		catch(ClassNotFoundException | SQLException e)
		{
			String errorMessage = "依赖数据库生成当前实例号出现错误";
			logger.error(errorMessage, e);
			throw new RuntimeException(errorMessage + ":" + e.getMessage());
		}
		catch (IOException e) {
			String errorMessage = "实例名存储文件:" + clusterNameFile.getAbsolutePath() + "创建失败!请检查是否有路径的读写权限或其它问题";
			logger.error(errorMessage, e);
			throw new RuntimeException(errorMessage + ":" + e.getMessage());
		} finally {
		}
		
	}

}
