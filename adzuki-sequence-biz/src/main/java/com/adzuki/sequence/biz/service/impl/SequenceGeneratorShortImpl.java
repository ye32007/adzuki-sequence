package com.adzuki.sequence.biz.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.adzuki.sequence.biz.utils.DateTimeUtil;
import com.adzuki.sequence.biz.utils.InitSystemEvnAndProp;
import com.adzuki.sequence.biz.utils.MappedFileUtil;
import com.adzuki.sequence.biz.utils.NumberUtil;
import com.adzuki.sequence.service.SequenceGeneratorShort;
import com.alibaba.dubbo.config.annotation.Service;

@Service(interfaceClass = SequenceGeneratorShort.class)
@Component("sequenceGeneratorShortImpl")
public class SequenceGeneratorShortImpl extends SequenceGeneratorAble implements SequenceGeneratorShort {

	private final static Logger logger = LoggerFactory.getLogger(SequenceGeneratorShortImpl.class.getName());
	final static String baseDir = InitSystemEvnAndProp.systemEvnAndProp.get("catalina.base")+ "/cluster/short/";
	final static Map<String,SeqModle> map=new HashMap<String,SeqModle>();
	final static Object lockObject=new Object();
	final static int seqMax = 999999;
	final static int seqLen = 6; 
	
	
	@SuppressWarnings("rawtypes")
	public static Map getMap() {
		return map;
	}

	@Override
	public String getNextUUID(String seqName) {
		return clusterName + handler(seqName);
	}

	@Override
	protected String handler(String seqName) {
		int val = 0;
		if(map.get(seqName)==null){
			synchronized (lockObject) {
				if(null == map.get(seqName)){
					SeqModle modle = new SeqModle();
					modle.setLastTime(DateTimeUtil.getCurrentDate());
					MappedFileUtil mapped = new MappedFileUtil(seqName, baseDir);
					modle.setMappedFileUtil(mapped);
					//文件时间，文件内容
					if(mapped.readLength()>0 && DateTimeUtil.isToDay(mapped.readModifyTime())){
						String s = mapped.readData().trim();
						int cur = Integer.parseInt(s);
						logger.info(seqName+" read currValue from file : "+s);
						modle.getAtomicInteger().set(cur+100);//标识一下重启,断电加上步长即可
					}
					map.put(seqName,modle);
					logger.info(seqName+" add size : "+map.size());
				}
			}
		}
		long day = DateTimeUtil.getCurrentDate();
		if(day != map.get(seqName).getLastTime() && map.get(seqName).getAtomicInteger().get()>800000){
			synchronized (map.get(seqName)) {
				if(day != map.get(seqName).getLastTime() && map.get(seqName).getAtomicInteger().get()>800000){
					logger.info(seqName + " new time period : "+val);
					map.get(seqName).getAtomicInteger().set(0);
					map.get(seqName).setLastTime(day);
					map.get(seqName).getMappedFileUtil().setInitWRITE_INTERVAL();
				}
			}
		}
		AtomicInteger atomic = map.get(seqName).getAtomicInteger();
		val = atomic.incrementAndGet();
		if(val>seqMax){
			logger.error(seqName + " exceeded sequence limit."+val);
			throw new RuntimeException(seqName + " exceeded sequence limit."+val);
		}else if(val>800000){
			logger.warn(seqName + " exceeded sequence warning."+val);
		}
		String data = NumberUtil.format(val,seqLen);
		map.get(seqName).getMappedFileUtil().writeData(data.getBytes());
		return data;
	}

}
