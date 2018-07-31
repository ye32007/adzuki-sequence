package com.adzuki.sequence.service;

public interface SequenceGeneratorHourRepeat {
	
	/**
	 * 一小时内一个节点可产生100w不重复序列值
	 * 16位长度，生成规则：8位日期 + 2位实例 + 6位序列
	 * @param seqName 业务名称
	 * @return
	 */
	String getNextUUID(String seqName);
}
