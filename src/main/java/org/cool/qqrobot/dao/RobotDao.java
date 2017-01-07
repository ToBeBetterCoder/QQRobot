package org.cool.qqrobot.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.cool.qqrobot.entity.AutoReply;
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
	AutoReply queryAutoReplyNames(String account) throws Exception;

	void insertOrUpdateReplyNameList(@Param("list") List<Map<String, Object>> list, @Param("selfUiu") String selfUiu) throws Exception;

	void disableReplyNameList(@Param("list") List<Map<String, Object>> list, @Param("selfUiu") String selfUiu) throws Exception;

	int hasAutoReply(String selfUiu) throws Exception;
	
	void initAutoReply(@Param("selfUiu") String selfUiu, @Param("isAutoReply") int isAutoReply, @Param("isSpecial") int isSpecial) throws Exception;
	
	void updateIsAutoReply(@Param("autoReplyfalg") int autoReplyfalg, @Param("selfUiu") String selfUiu) throws Exception;

	void updateIsSpecial(@Param("specialfalg") int specialfalg, @Param("selfUiu") String selfUiu) throws Exception;

	void updateQuitTime(Integer id) throws Exception;

	
}
