package org.cool.qqrobot.exception;

import java.util.HashMap;
import java.util.Map;

public class RobotException extends RuntimeException {

	private static final long serialVersionUID = -5536914896540424869L;

	private Map<String, Object> map;
	
	public RobotException(String message, Throwable cause) {
		super(message, cause);
	}

	public RobotException(String message) {
		super(message);
	}

	public RobotException(Throwable cause) {
		super(cause);
	}
	
	public RobotException() {
		super("用户未登录");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		this.map = map;
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}
}
