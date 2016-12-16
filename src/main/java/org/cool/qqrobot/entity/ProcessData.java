package org.cool.qqrobot.entity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.cool.qqrobot.common.Const;
import org.cool.qqrobot.http.MyHttpClient;

/**
 * 存放过程数据
 * transient关键字修饰的属性不会被序列化
 * @author zhoukl
 *
 */
public class ProcessData {
	private boolean isLogin = false;
	private transient String imageCode;
	private String ptwebqq;
	private String selfUiu;
	private String psessionid;
	private String vfwebqq;
	// 每个用户登录都有属于自己的httpClient
	private transient MyHttpClient myHttpClient = MyHttpClient.getNewInstance();
	// 好友列表
	private transient Map<String, Object> friendsMap = new HashMap<String, Object>();
	// 群列表
	private transient Map<String, Object> groupsMap = new HashMap<String, Object>();
	// 讨论组
	private transient Map<String, Object> discussesMap = new HashMap<String, Object>();
	// 个人信息
	private transient UserInfo userInfo;
	// 在线好友
	private transient Map<String, Object> onlineBuddiesMap = new HashMap<String, Object>();
	// 自动回复列表 根据用户昵称查询用户uin，保存后，每次回复消息首先检查是否允许自动回复该uin
	private AutoReply autoReply;
	
	public String getPtwebqq() {
		return ptwebqq;
	}
	public void setPtwebqq(String ptwebqq) {
		this.ptwebqq = ptwebqq;
	}
	public String getSelfUiu() {
		return selfUiu;
	}
	public void setSelfUiu(Double selfUiu) {
		this.selfUiu = new DecimalFormat("#").format(selfUiu);
	}
	public String getPsessionid() {
		return psessionid;
	}
	public void setPsessionid(String psessionid) {
		this.psessionid = psessionid;
	}
	public String getVfwebqq() {
		return vfwebqq;
	}
	public void setVfwebqq(String vfwebqq) {
		this.vfwebqq = vfwebqq;
	}
	public boolean isLogin() {
		return isLogin;
	}
	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}
	public String getImageCode() {
		return imageCode;
	}
	public void setImageCode(String imageCode) {
		this.imageCode = imageCode;
	}
	public MyHttpClient getMyHttpClient() {
		return myHttpClient;
	}
	public Map<String, Object> getFriendsMap() {
		return friendsMap;
	}
	public void setFriendsMap(Map<String, Object> friendsMap) {
		this.friendsMap = friendsMap;
	}
	public Map<String, Object> getGroupsMap() {
		return groupsMap;
	}
	public void setGroupsMap(Map<String, Object> groupsMap) {
		this.groupsMap = groupsMap;
	}
	public Map<String, Object> getDiscussesMap() {
		return discussesMap;
	}
	public void setDiscussesMap(Map<String, Object> discussesMap) {
		this.discussesMap = discussesMap;
	}
	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	public Map<String, Object> getOnlineBuddiesMap() {
		return onlineBuddiesMap;
	}
	public void setOnlineBuddiesMap(Map<String, Object> onlineBuddiesMap) {
		this.onlineBuddiesMap = onlineBuddiesMap;
	}
	public AutoReply getAutoReply() {
		return autoReply;
	}
	public void setAutoReply(AutoReply autoReply) {
		if (null != autoReply) {
			uinSet(autoReply);
		}
		this.autoReply = autoReply;
	}
	// 根据名称匹配uin
	private void uinSet(AutoReply autoReply) {
		List<ReplyName> replyNameList = autoReply.getReplyNameList();
		for (ReplyName replyName : replyNameList) {
			if (Const.PERSON == replyName.getType()) {
				// new ArrayList<Map<String, Object>>() 防止空指针
				List<Map<String, Object>> marknamesList = (List<Map<String, Object>>) MapUtils.getObject(this.friendsMap, Const.MARK_NAMES, new ArrayList<Map<String, Object>>());
				for (Map<String, Object> map : marknamesList) {
					if (replyName.getMarkName().equals(MapUtils.getString(map, Const.MARK_NAME))) {
						replyName.setUin(new DecimalFormat("#").format(MapUtils.getObject(map, Const.UIN)));
					}
				}
				// marknames找不到再去info找
				if (null == replyName.getUin()) {
					List<Map<String, Object>> infoList = (List<Map<String, Object>>) MapUtils.getObject(this.friendsMap, Const.INFO, new ArrayList<Map<String, Object>>());
					for (Map<String, Object> map : infoList) {
						if (replyName.getMarkName().equals(MapUtils.getString(map, Const.NICK))) {
							replyName.setUin(new DecimalFormat("#").format(MapUtils.getObject(map, Const.UIN)));
						}
					}
				}
			}
			if (Const.GROUP == replyName.getType()) {
				List<Map<String, Object>> gnameList = (List<Map<String, Object>>) MapUtils.getObject(this.groupsMap, Const.G_NAME_LIST, new ArrayList<Map<String, Object>>());
				for (Map<String, Object> map : gnameList) {
					if (replyName.getMarkName().equals(MapUtils.getString(map, Const.NAME))) {
						replyName.setUin(new DecimalFormat("#").format(MapUtils.getObject(map, Const.G_ID)));
					}
				}
			}
			if (Const.DISCU == replyName.getType()) {
				List<Map<String, Object>> dnameList = (List<Map<String, Object>>) MapUtils.getObject(this.discussesMap, Const.D_NAME_LIST, new ArrayList<Map<String, Object>>());
				for (Map<String, Object> map : dnameList) {
					if (replyName.getMarkName().equals(MapUtils.getString(map, Const.NAME))) {
						replyName.setUin(new DecimalFormat("#").format(MapUtils.getObject(map, Const.D_ID)));
					}
				}
			}
		}
	}
}
