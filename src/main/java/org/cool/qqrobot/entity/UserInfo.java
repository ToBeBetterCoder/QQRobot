package org.cool.qqrobot.entity;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.cool.qqrobot.common.Const;

public class UserInfo {
	/*{
	    "result": {
	        "account": 3198803867,
	        "allow": 1,
	        "birthday": {
	            "day": 8,
	            "month": 6,
	            "year": 2015
	        },
	        "blood": 0,
	        "city": "",
	        "college": "",
	        "constel": 0,
	        "country": "冰岛",
	        "email": "",
	        "face": 594,
	        "gender": "male",
	        "homepage": "",
	        "lnick": "",
	        "mobile": "",
	        "nick": "周凯林",
	        "occupation": "",
	        "personal": "不会解决一切重启电脑不能解决的问题",
	        "phone": "",
	        "province": "",
	        "shengxiao": 0,
	        "uin": 3198803867,
	        "vfwebqq": "5669b3d0fab3ab5751fb8e0228678b1074cb3899fee31f7133bf71c934f6034f4e8576964ab4ffba",
	        "vip_info": 0
	    },
	    "retcode": 0
	}*/
	// 主键自增id
	private Integer id;
	// QQ号
	private String account;
	// 性别 男：male 女：female
	private String gender;
	// 昵称
	private String nick;
	// 个性签名
	private String lnick;
	// 完整JSON
	private String detail;
	// 创建时间
	private Date createTime;
	// 退出登录时间
	private Date quitTime;
	
	public UserInfo(Map<String, Object> userInfoMap, String userInfoJson) {
		super();
		@SuppressWarnings("unchecked")
		
		Map<String, Object> userInfoResult = MapUtils.getMap(userInfoMap, Const.RESULT);
		// 转换Double科学计数为String类型
		this.account = new DecimalFormat("#").format(MapUtils.getObject(userInfoResult, Const.ACCOUNT));
		this.gender = MapUtils.getString(userInfoResult, Const.GENDER);
		this.nick = MapUtils.getString(userInfoResult, Const.NICK);
		this.lnick = MapUtils.getString(userInfoResult, Const.LNICK);
		this.detail = userInfoJson;
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
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getLnick() {
		return lnick;
	}
	public void setLnick(String lnick) {
		this.lnick = lnick;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getQuitTime() {
		return quitTime;
	}
	public void setQuitTime(Date quitTime) {
		this.quitTime = quitTime;
	}
}
