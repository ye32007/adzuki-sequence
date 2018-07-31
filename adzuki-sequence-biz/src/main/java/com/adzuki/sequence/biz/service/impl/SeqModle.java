package com.adzuki.sequence.biz.service.impl;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import com.adzuki.sequence.biz.utils.MappedFileUtil;

public class SeqModle implements Serializable {

	private static final long serialVersionUID = -8888036080864275698L;
	
	private AtomicInteger atomicInteger = new AtomicInteger();
	private MappedFileUtil mappedFileUtil;
	private long lastTime;
	public AtomicInteger getAtomicInteger() {
		return atomicInteger;
	}
	public void setAtomicInteger(AtomicInteger atomicInteger) {
		this.atomicInteger = atomicInteger;
	}
	public MappedFileUtil getMappedFileUtil() {
		return mappedFileUtil;
	}
	public void setMappedFileUtil(MappedFileUtil mappedFileUtil) {
		this.mappedFileUtil = mappedFileUtil;
	}
	public long getLastTime() {
		return lastTime;
	}
	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}
	@Override
	public String toString() {
		return " [atomicInteger=" + atomicInteger.get() + ", lastTime=" + lastTime + "]";
	}
	
	
}
