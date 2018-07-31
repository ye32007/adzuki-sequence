package com.adzuki.sequence.service;

public interface SequenceGeneratorDayRepeat {
	
	/**
	 * 一天内一个节点可产生100w不重复序列值
	 * 14位长度，生成规则：6位日期 + 2位实例 + 6位序列
	 * @param seqName 业务名称
	 * @return
	 */
	String getNextUUID(String seqName);
}
