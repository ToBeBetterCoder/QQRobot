package org.cool.qqrobot.service;

import org.cool.qqrobot.entity.MyHttpResponse;
import org.cool.qqrobot.entity.ProcessData;

public interface RobotService {
	/**
	 * 获取登录二维码
	 * @param processData
	 * @return
	 */
	MyHttpResponse getCode(ProcessData processData);
}
