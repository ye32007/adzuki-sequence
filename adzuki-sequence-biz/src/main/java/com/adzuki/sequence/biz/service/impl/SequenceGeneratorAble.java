package com.adzuki.sequence.biz.service.impl;

import com.adzuki.sequence.biz.utils.InitSystemEvnAndProp;

public abstract class SequenceGeneratorAble {
	
	final static String clusterName = InitSystemEvnAndProp.clusterName;
	
	final static int MILLISOFDAY = 1000*60*60*24;

	protected abstract String handler(String seqName);

	public static String getClustername() {
		return clusterName;
	}

}
