package org.cool.qqrobot.service;

import java.util.Map;

import org.cool.qqrobot.entity.MyHttpResponse;
import org.cool.qqrobot.entity.ProcessData;

public interface RobotService {
	/**
	 * 获取登录二维码
	 * @param processData
	 * @return
	 */
	MyHttpResponse getCode(ProcessData processData);

	/**
	 * 构建好友列表
	 * @param obj
	 * @return
	 */
	
	Map<String, Object> buildFriendsList(ProcessData obj);
	/**
	 * 构建讨论组列表
	 * @param obj
	 * @return
	 */
	Map<String, Object> buildDiscussesList(ProcessData obj);
	
	/**
	 * 构建群列表
	 * @param obj
	 * @return
	 */
	Map<String, Object> buildGroupsList(ProcessData obj);
}
