package com.adzuki.sequence.biz.utils;


import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequenceFileMapUtil {
	
	private static Logger logger = LoggerFactory.getLogger("SequenceFileMapUtil");
	
	//存储序列号与内存页对应的Map
	public static Map<String,MappedByteBuffer> mappedByteBufferMap=new HashMap<String,MappedByteBuffer>();
	
	//存储序列号与读写锁对应的Map
	public static Map<String,ReadWriteLock> readWriteLockMap=new HashMap<String,ReadWriteLock>();
	
	static {
		Set<OpenOption> options=new HashSet<OpenOption>();
		options.add(StandardOpenOption.CREATE);
		options.add(StandardOpenOption.READ);
		options.add(StandardOpenOption.WRITE);
		options.add(StandardOpenOption.DSYNC);
		Set<PosixFilePermission> perms=new HashSet<PosixFilePermission>();
		perms.add(PosixFilePermission.OWNER_WRITE);
		perms.add(PosixFilePermission.OWNER_READ);
		FileAttribute<Set<PosixFilePermission>> attrs=PosixFilePermissions.asFileAttribute(perms);
		//Path seqPath=Paths.get("/home/data/workspace/demo/seqFiles/jiaoyi.seq");
		String catalinaBase = InitSystemEvnAndProp.systemEvnAndProp.get("catalina.base");
		File seqFilePathDir=new File(catalinaBase+"/seqFiles");
		
		if(seqFilePathDir.exists())
		{
			//如果文件夹已经存在
			logger.info("序列文件夹已经存在:"+seqFilePathDir.getAbsolutePath());
		}
		else
		{
			boolean isSeqFilePathDirCreated=seqFilePathDir.mkdir();
			if(isSeqFilePathDirCreated)
			{
				logger.info("文件夹:"+seqFilePathDir.getAbsolutePath()+"创建成功");
			}
			else
			{
				String errorMessage="文件夹:"+seqFilePathDir.getAbsolutePath()+"创建失败!请检查是否有路径的读写权限!";
				logger.error(errorMessage);
				throw new RuntimeException(errorMessage);
			}
		}
		//序列文件的文件名为序列名字+实例名.seq
		Path seqFilePath=Paths.get(seqFilePathDir.getAbsolutePath(),"jiaoyi"+InitSystemEvnAndProp.clusterName+".seq");
		FileChannel seqFileChannel=null;
		try {
			seqFileChannel = FileChannel.open(seqFilePath,options,attrs);
			MappedByteBuffer mappedByteBuffer=seqFileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 64);
			final ReadWriteLock rwl = new ReentrantReadWriteLock();
			
			//将初始化的信息写到Map中
			mappedByteBufferMap.put("jiaoyi", mappedByteBuffer);
			readWriteLockMap.put("jiaoyi", rwl);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param mappedByteBuffer 内存页文件
	 * @param rwl	读写锁
	 * @param count	获取的序列个数
	 * @param	minValue 序列的最小值,应该为大于等于0的数
	 * @param maxValue 序列的最大值,应该为大于等于0的数
	 * @return 存放获取的序列的数组
	 * @throws IOException
	 */
	public static long[] getAndAddIntervals(MappedByteBuffer mappedByteBuffer,ReadWriteLock rwl,int count,long minValue,long max_value) throws IOException{
		try{
		rwl.writeLock().lock();
		mappedByteBuffer.clear();
		long begin=mappedByteBuffer.getLong();
		
		//当序列文件被第一次读取的时候由于没有内容获取的long类型必然为0L，这时候初始化为最小值
		if(0L==begin)
		{
			begin=minValue;
		}
		
		long addEnd=begin+count;
		long end=addEnd;
		//当long类型上溢出时为负数
		if(max_value<=addEnd || 0L>=addEnd)
		{
			mappedByteBuffer.clear();
			//设置下一次读写的值为0
			mappedByteBuffer.putLong(minValue);
			end=max_value;
		}else
		{
			mappedByteBuffer.clear();
			//设置下一次读写的值为0
			mappedByteBuffer.putLong(end+1);
		}
		//阻塞,写入磁盘
		//mappedByteBuffer.force();
		return (end!=begin)?(new long[]{begin,end}):(new long[]{begin});
		}finally{
			rwl.writeLock().unlock();
		}
	}
}
