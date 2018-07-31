package com.adzuki.sequence.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.adzuki.sequence.service.SequenceGeneratorDayRepeat;
import com.adzuki.sequence.service.SequenceGeneratorHourRepeat;
import com.adzuki.sequence.service.SequenceGeneratorMinuteRepeat;
import com.adzuki.sequence.service.SequenceGeneratorNonRepeat;
import com.adzuki.sequence.service.SequenceGeneratorShort;

@RestController
public class SequenceController {
	
	@Autowired
	private SequenceGeneratorDayRepeat sequenceGeneratorDayRepeatImpl;
	@Autowired
	private SequenceGeneratorHourRepeat sequenceGeneratorHourRepeatImpl;
	@Autowired
	private SequenceGeneratorMinuteRepeat sequenceGeneratorMinuteRepeatImpl;
	@Autowired
	private SequenceGeneratorNonRepeat sequenceGeneratorNonRepeatImpl;
	@Autowired
	private SequenceGeneratorShort sequenceGeneratorShortImpl;
	
	/**
	 * 按照序列类型和业务类型返回序列号
	 * day : 每节点每天0~999999循环，即每节点每天100w
	 * hour : 每节点每小时0~999999循环，即每节点每每小时100w
	 * minute :
	 * second :
	 * short : 
	 * @param type
	 * @param seqName
	 * @return
	 */
	@RequestMapping(method = {RequestMethod.GET}, path = "/nextUUID/{type}/{seqName}")
	public String getNextUUID(@PathVariable("type") String type,@PathVariable("seqName") String seqName) {
		if("day".equals(type)){//每节点每天0~999999循环，即每节点每天100w
			return sequenceGeneratorDayRepeatImpl.getNextUUID(seqName);
		}else if("hour".equals(type)){//每节点每小时0~999999循环，即每节点每每小时100w
			return sequenceGeneratorHourRepeatImpl.getNextUUID(seqName);
		}else if("minute".equals(type)){//每节点每分钟0~999999循环，即每节点每分钟100w
			return sequenceGeneratorMinuteRepeatImpl.getNextUUID(seqName);
		}else if("second".equals(type)){//每节点每秒0~999999循环，即每节点每秒100w
			return sequenceGeneratorNonRepeatImpl.getNextUUID(seqName);
		}else if("short".equals(type)){//可能出现的重复性，由调用端自行解决。该序列只从0~999999循环。
			return sequenceGeneratorShortImpl.getNextUUID(seqName);
		}
		return null;
	}


}
