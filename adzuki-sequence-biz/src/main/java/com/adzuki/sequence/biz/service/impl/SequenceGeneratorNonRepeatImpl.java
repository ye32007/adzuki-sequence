package com.adzuki.sequence.biz.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.adzuki.sequence.biz.utils.DateTimeUtil;
import com.adzuki.sequence.biz.utils.NumberUtil;
import com.adzuki.sequence.service.SequenceGeneratorNonRepeat;
import com.alibaba.dubbo.config.annotation.Service;

@Service(interfaceClass = SequenceGeneratorNonRepeat.class)
@Component("sequenceGeneratorNonRepeatImpl")
public class SequenceGeneratorNonRepeatImpl extends SequenceGeneratorAble implements SequenceGeneratorNonRepeat {

	private final static Logger logger = LoggerFactory.getLogger(SequenceGeneratorNonRepeatImpl.class.getName());
	final static Map<String,AtomicInteger> map = new HashMap<String,AtomicInteger>();
	final static Object lockObject = new Object();
	final static int seqMax = 99999;
	final static int seqLen = 5; 
	
	@SuppressWarnings("rawtypes")
	public static Map getMap() {
		return map;
	}
	
	@Override
	public String getNextUUID(String seqName) {
		return DateTimeUtil.getCurrentSecond()+ clusterName + handler(seqName);
	}

	/**
	 * 若重启，秒数至少+1了，故不可能重复，和day,hour,minute不同
	 */
	@Override
	protected String handler(String seqName) {
		int val = 0;
		if(map.get(seqName)==null){
			synchronized (lockObject) {
				if(null==map.get(seqName))
				{
					map.put(seqName,new AtomicInteger());
					logger.info(seqName+" add size : "+map.size());
				}
			}
		}
		AtomicInteger atomic = map.get(seqName);
		val = atomic.incrementAndGet();
		if(val>seqMax){
			synchronized (atomic) {
				if(atomic.get()>=seqMax){
					logger.info(seqName + " new time period : "+val);
					atomic.set(0);
				}
			}
			val = atomic.incrementAndGet();
		}
		return NumberUtil.format(val,seqLen);
	}

}
