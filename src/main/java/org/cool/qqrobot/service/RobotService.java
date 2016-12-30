package org.cool.qqrobot.service;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.cool.qqrobot.entity.MyHttpResponse;
import org.cool.qqrobot.entity.ProcessData;
import org.cool.qqrobot.exception.RobotException;

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

	/**
	 * 更新回复列表
	 * @param paramMap
	 * @param processDataSession 
	 * @return
	 */
	void updateReplyNameList(Map<String, Object> paramMap, ProcessData processDataSession) throws RobotException;
	/**
	 * 更新是否自动回复
	 * @param autoReplyfalg
	 * @param processDataSession
	 * @return
	 */
	boolean updateIsAutoReply(int autoReplyfalg, ProcessData processDataSession);
	/**
	 * 更新是否自定义回复
	 * @param specialfalg
	 * @param processDataSession
	 * @return
	 */
	boolean updateIsSpecial(int specialfalg, ProcessData processDataSession);
	/**
	 * 传递session
	 * @param session
	 */
	void sessionSetter(HttpSession session);
}
