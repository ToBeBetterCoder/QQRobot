package org.cool.qqrobot.common;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.cool.qqrobot.entity.ProcessData;
/**
 * 用户是否登录不仅仅根据session判断（因为可能存在多点登录，多个session，一端退出后，session被清除，但是另一个终端session并未清除），还要根据缓存数据判断（缓存数据只有一份，根据qq号存储）
 * @author zhoukl
 *
 */
public class CacheMap {
	public static ConcurrentHashMap<String, ProcessData> processDataMap = new ConcurrentHashMap<String, ProcessData>();
	public static ConcurrentHashMap<String, Future<?>> threadFuturMap = new ConcurrentHashMap<String, Future<?>>();
	/**
	 * 判断是否在线
	 * @param processData
	 * @return
	 */
	public static boolean isOnline(ProcessData processData) {
		if (null != processData.getSelfUiu() && processDataMap.containsKey(processData.getSelfUiu()) && processDataMap.get(processData.getSelfUiu()).isLogin()) {
			return true;
		}
		return false;
	}
	/**
	 * 判断是否已经获取过二维码
	 * @param processData
	 * @return
	 */
	public static boolean hasGetCode(ProcessData processData) {
		if (!isOnline(processData) && processData.isGetCode()) {
			return true;
		}
		return false;
	}
	
}
