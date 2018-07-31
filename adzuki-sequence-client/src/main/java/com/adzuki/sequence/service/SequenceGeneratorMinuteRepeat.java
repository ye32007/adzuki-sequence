package com.adzuki.sequence.service;

public interface SequenceGeneratorMinuteRepeat {
	
	/**
	 * 一分钟内一个节点可产生100w不重复序列值
	 * 18位长度，生成规则：10位日期 + 2位实例 + 6位序列
	 * @param seqName 业务名称
	 * @return
	 */
	String getNextUUID(String seqName);
}
