package org.cool.qqrobot.dao;

import java.util.Set;

import org.cool.qqrobot.entity.AutoReply;
import org.cool.qqrobot.entity.ReplyName;
import org.cool.qqrobot.entity.UserInfo;

public interface RobotDao {
	/**
	 * 记录用户登录信息
	 * @param userInfo
	 * @return
	 */
	int addLoginInfo(UserInfo userInfo);
	
	/**
	 * 查询自动回复名单
	 * @param account QQ号
	 * @return 账户下允许自动回复的昵称名单
	 */
	AutoReply queryAutoReplyNames(String account);
	
}
