package com.adzuki.sequence.service;

public interface SequenceGeneratorShort {

	/**
	 * 短序列，8位长度，日百万级，生成规则：2位实例+6位序列)
	 * @param seqName 业务名称
	 * @return
	 */
	String getNextUUID(String seqName);
}
