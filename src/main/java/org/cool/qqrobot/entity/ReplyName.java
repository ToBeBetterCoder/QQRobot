package org.cool.qqrobot.entity;

public class ReplyName {
	private Integer pid;
	private String markName;
	//0 私聊消息 1讨论组 2群
	private byte type;
	// 每次登录都不一样，需重新匹配，故不记录在数据库
	private String uin;
	public Integer getPid() {
		return pid;
	}
	public void setPid(Integer pid) {
		this.pid = pid;
	}
	public String getMarkName() {
		return markName;
	}
	public void setMarkName(String markName) {
		this.markName = markName;
	}
	public String getUin() {
		return uin;
	}
	public void setUin(String uin) {
		this.uin = uin;
	}
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "ReplyName [pid=" + pid + ", markName=" + markName + ", uin=" + uin + "]";
	}
}
