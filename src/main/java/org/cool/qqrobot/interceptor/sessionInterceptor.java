package org.cool.qqrobot.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cool.qqrobot.common.CacheMap;
import org.cool.qqrobot.common.Const;
import org.cool.qqrobot.common.RobotCodeEnums;
import org.cool.qqrobot.dto.RobotResult;
import org.cool.qqrobot.entity.ProcessData;
import org.cool.qqrobot.exception.RobotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

public class sessionInterceptor implements HandlerInterceptor {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception, RobotException {
		ProcessData processDataSession = (ProcessData) request.getSession().getAttribute(Const.PROCESS_DATA);
		if (null == processDataSession) {
			logger.debug("session invalid");
			sessionInvalidReturn(response);
			return false;
		} else if (processDataSession.isLogin() && !CacheMap.isOnline(processDataSession)) {
			// session拦截时也要判断缓存数据是否存在，如果session存在，但是缓存数据已不存在，则也要清除当前冗余session
			request.getSession().removeAttribute(Const.PROCESS_DATA);
			sessionInvalidReturn(response);
			return false;
		}
		return true;
	}

	private void sessionInvalidReturn(HttpServletResponse response) {
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			logger.error("session invalid return", e);
		}
		Gson gson = new Gson();
    	out.print(gson.toJson(new RobotResult<Map<String, Object>>(true, RobotCodeEnums.SESSION_EXPIRED.getCode(), RobotCodeEnums.SESSION_EXPIRED.getCodeInfo())));
	    out.close();
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}

}
