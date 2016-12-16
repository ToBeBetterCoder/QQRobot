package org.cool.qqrobot.common;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.cool.qqrobot.entity.ProcessData;

public class CacheMap {
	public static ConcurrentHashMap<String, ProcessData> processDataMap = new ConcurrentHashMap<String, ProcessData>();
	public static ConcurrentHashMap<String, Future<?>> threadFuturMap = new ConcurrentHashMap<String, Future<?>>();
}
