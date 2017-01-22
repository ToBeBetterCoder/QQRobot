package org.cool.qqrobot.web;

import java.util.HashMap;
import java.util.Map;

import org.cool.qqrobot.common.Const;
import org.cool.qqrobot.common.RobotCodeEnums;
import org.cool.qqrobot.dto.RobotResult;

public class BaseController {
	protected RobotResult<Map<String, Object>> successResult() {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put(Const.INFO, RobotCodeEnums.REQUEST_SUCCESS.getCodeInfo());
		return new RobotResult<Map<String, Object>>(true, RobotCodeEnums.REQUEST_SUCCESS.getCode(), responseMap);
	}
	
	protected RobotResult<Map<String, Object>> successResult(Map responseMap) {
		return new RobotResult<Map<String, Object>>(true, RobotCodeEnums.REQUEST_SUCCESS.getCode(), responseMap);
	}
	
	protected RobotResult<Map<String, Object>> exceptionResult() {
		return new RobotResult<Map<String, Object>>(true, RobotCodeEnums.REQUEST_FAIL.getCode(), RobotCodeEnums.REQUEST_FAIL.getCodeInfo());
	}
}
