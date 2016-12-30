package org.cool.qqrobot.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AutoReply {
	// 主键
	private Integer id;
	// QQ号
	private String account;
	// 是否允许自动回复 1：true （得选择包装类型，不然页面获取不到属性）
	private Boolean isAutoReply = false;
	private Date createTime;
	private Date updateTime;
	// 是否自定义回复列表
	private Boolean isSpecial = false;
	// 自动回复好友/群/讨论组列表（一对多）
	private List<ReplyName> replyNameList = new ArrayList<ReplyName>();
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public Boolean getIsAutoReply() {
		return isAutoReply;
	}
	public void setIsAutoReply(Boolean isAutoReply) {
		this.isAutoReply = isAutoReply;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Boolean getIsSpecial() {
		return isSpecial;
	}
	public void setIsSpecial(Boolean isSpecial) {
		this.isSpecial = isSpecial;
	}
	public List<ReplyName> getReplyNameList() {
		return replyNameList;
	}
	public void setReplyNameList(List<ReplyName> replyNameList) {
		this.replyNameList = replyNameList;
	}
}
