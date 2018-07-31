package com.adzuki.sequence.service;

public interface SequenceGeneratorNonRepeat {
	
	
	/**
	 * 一秒内一个节点可产生100w不重复序列值
	 * 20位长度，生成规则：12位日期 + 2位实例 + 6位序列
	 * @param seqName 业务名称
	 * @return
	 */
	String getNextUUID(String seqName);
}
