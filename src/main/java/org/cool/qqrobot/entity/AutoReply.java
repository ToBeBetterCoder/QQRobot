package org.cool.qqrobot.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.cool.qqrobot.common.AutoReplySetting;

public class AutoReply {
	// 主键
	private Integer id;
	// QQ号
	private String account;
	// 是否允许自动回复 （默认：是） （得选择包装类型，不然页面获取不到属性）
	private Boolean isAutoReply = true;
	private Date createTime;
	private Date updateTime;
	// 是否自定义回复列表（默认：否）
	private Boolean isSpecial = true;
	// 自动回复好友/群/讨论组列表（一对多）
	private List<ReplyName> replyNameList = new ArrayList<ReplyName>();
	// Tips mybatis映射实体类必须有有个无参构造方法，不然反射不能构造对象，mybatis会报错：No constructor found in org.cool.qqrobot.entity.AutoReply...
	public AutoReply() {
		super();
	}
	public AutoReply(String account) {
		super();
		this.account = account;
	}
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
