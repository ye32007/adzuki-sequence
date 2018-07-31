package com.adzuki.sequence.biz.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SequenceWithTimeUtil {
	final static ReadWriteLock rwl= new ReentrantReadWriteLock();
	private static int sequence = 0;
	private static String lastDateTime = null;

	public static int[] getAndAddIntervals(int number, String currentDateTime) {
		if (currentDateTime.equals(lastDateTime)) {
			try {
				rwl.writeLock().tryLock(50,TimeUnit.MILLISECONDS);
				// 当在本分钟内时
				if (0==sequence) {
					throw new RuntimeException("单分钟内超过最大值99999999");
				}
				int begin = sequence;
				int addEnd = begin + number;
				int end = addEnd;
				sequence=end+1;
				if (9999999 <= addEnd) {
					// 设置下一次读写的值为0
					sequence = 0;
					end = 9999999;
				}
				return (end != begin) ? (new int[] { begin, end }) : (new int[] { begin });
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				rwl.writeLock().unlock();
			}
		} else {
			// 新的1分钟开始时
			try {
				rwl.writeLock().tryLock(50,TimeUnit.MILLISECONDS);
				// 设置下一次读写的值为0
				lastDateTime = currentDateTime;
				int begin = 0;
				int addEnd = begin + number;
				int end = addEnd;
				sequence=end+1;
				if (9999999 <= addEnd) {
					sequence = 0;
					end = 9999999;
				}
				return (end != begin) ? (new int[] { begin, end }) : (new int[] { begin });
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				rwl.writeLock().unlock();
			}
		}
		return null;
	}
}
