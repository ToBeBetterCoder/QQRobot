package org.cool.qqrobot.common;


/**
 * 图灵机器人API调用返回编码
 * @author zhoukl
 *
 */
public enum RobotCodeEnums {
	TEXT(100000, "文本类"),
	LINK(200000, "链接类"),
	NEW(302000, "新闻类"),
	COOK(308000, "菜谱类"),
	KEY_ERROR(40001, "参数key错误"),
	EMPTY_INFO_ERROR(40002, "请求内容info为空"),
	TIMES_LIMIT_ERROR(40004, "当天请求次数已使用完"),
	DATA_FORMATE_ERROR(40007, "数据格式异常"),
	DEFAULT_ERROR(-1, "无法识别异常编码"),
	
	REQUEST_SUCCESS(0, "操作成功"),
	REQUEST_FAIL(-1, "操作失败"),
	SESSION_EXPIRED(1, "会话过期，请重新登录~"),
	ILLEGAL_ACCESS(-2, "非法访问");
	private RobotCodeEnums(int code, String codeInfo) {
		this.code = code;
		this.codeInfo = codeInfo;
	}

	private int code;
	 
	private String codeInfo;

	public int getCode() {
		return code;
	}

	public String getCodeInfo() {
		return codeInfo;
	}
	
	public static RobotCodeEnums stateOf(int code) {
		for (RobotCodeEnums state : RobotCodeEnums.values()) {
			if (code == state.getCode()) {
				return state;
			}
		}
		return RobotCodeEnums.DEFAULT_ERROR;
	}
}
