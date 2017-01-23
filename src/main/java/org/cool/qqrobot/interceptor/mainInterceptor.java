package org.cool.qqrobot.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cool.qqrobot.common.Const;
import org.cool.qqrobot.common.IpUtil;
import org.cool.qqrobot.common.ProcessVar;
import org.cool.qqrobot.common.RobotCodeEnums;
import org.cool.qqrobot.dto.RobotResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

public class mainInterceptor implements HandlerInterceptor {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String userAgent = request.getHeader(Const.USER_AGENT);
		logger.info("request path={}", request.getServletPath());
		logger.info("remote IP={}", IpUtil.getIpAddr(request));
		logger.info("User-Agent={}", userAgent);
		ProcessVar.setProcessId(request.getSession().getId());
		if (null == userAgent) {
			logger.info("illegal access, user agent is null!");
			illegalAccessReturn(response);
			return false;
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}
	
	private void illegalAccessReturn(HttpServletResponse response) {
		response.setContentType(Const.CONTENT_TYPE_JSON);
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			logger.error("illegal access return", e);
		}
		Gson gson = new Gson();
    	out.print(gson.toJson(new RobotResult<Map<String, Object>>(true, RobotCodeEnums.ILLEGAL_ACCESS.getCode(), RobotCodeEnums.ILLEGAL_ACCESS.getCodeInfo())));
	    out.close();
	}
}
