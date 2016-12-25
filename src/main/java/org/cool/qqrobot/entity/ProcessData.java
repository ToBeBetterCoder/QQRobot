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
	// 成功登录前的二维码获取控制
	private boolean isGetCode = false;
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
	private transient List<Map<String, Object>> onlineBuddiesList = new ArrayList<Map<String, Object>>();
	// 自动回复列表 根据用户昵称查询用户uin，保存后，每次回复消息首先检查是否允许自动回复该uin
	private AutoReply autoReply;
	// 页面展示的好友列表
	private Map<String, Object> friendsViewMap = new HashMap<String, Object>();
	// 页面展示的群列表
	private Map<String, Object> groupsViewMap = new HashMap<String, Object>();
	// 页面展示的讨论组列表
	private Map<String, Object> discussesViewMap = new HashMap<String, Object>();
	public boolean isGetCode() {
		return isGetCode;
	}
	public void setGetCode(boolean isGetCode) {
		this.isGetCode = isGetCode;
	}
	public String getPtwebqq() {
		return ptwebqq;
	}
	public void setPtwebqq(String ptwebqq) {
		this.ptwebqq = ptwebqq;
	}
	public String getSelfUiu() {
		return selfUiu;
	}
	public void setSelfUiu(Object selfUiu) {
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
	public List<Map<String, Object>> getOnlineBuddiesList() {
		return onlineBuddiesList;
	}
	public void setOnlineBuddiesList(List<Map<String, Object>> onlineBuddiesList) {
		this.onlineBuddiesList = onlineBuddiesList;
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
	public Map<String, Object> getFriendsViewMap() {
		return friendsViewMap;
	}
	public void setFriendsViewMap(Map<String, Object> friendsViewMap) {
		this.friendsViewMap = friendsViewMap;
	}
	public Map<String, Object> getGroupsViewMap() {
		return groupsViewMap;
	}
	public void setGroupsViewMap(Map<String, Object> groupsViewMap) {
		this.groupsViewMap = groupsViewMap;
	}
	public Map<String, Object> getDiscussesViewMap() {
		return discussesViewMap;
	}
	public void setDiscussesViewMap(Map<String, Object> discussesViewMap) {
		this.discussesViewMap = discussesViewMap;
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
	/**
	 * 更新对象
	 * @param processData
	 */
	public void update(ProcessData processData) {
		this.isGetCode = processData.isGetCode;
		this.isLogin = processData.isLogin;
		this.imageCode = processData.getImageCode();
		this.ptwebqq = processData.getPtwebqq();
		this.selfUiu = processData.getSelfUiu();
		this.psessionid = processData.getPsessionid();
		this.vfwebqq = processData.getVfwebqq();
		this.myHttpClient = processData.getMyHttpClient();
		this.friendsMap = processData.getFriendsMap();
		this.groupsMap = processData.getGroupsMap();
		this.discussesMap = processData.getDiscussesMap();
		this.userInfo = processData.getUserInfo();
		this.onlineBuddiesList = processData.getOnlineBuddiesList();
		this.autoReply = processData.getAutoReply();
		this.friendsViewMap = processData.getFriendsViewMap();
		this.groupsViewMap = processData.getGroupsViewMap();
		this.discussesViewMap = processData.getDiscussesViewMap();
	}
}
