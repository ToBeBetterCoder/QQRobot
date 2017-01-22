package org.cool.qqrobot.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cool.qqrobot.common.Const;
import org.cool.qqrobot.common.IpUtil;
import org.cool.qqrobot.common.ProcessVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class mainInterceptor implements HandlerInterceptor {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		logger.info("request path={}", request.getServletPath());
		logger.info("remote IP={}", IpUtil.getIpAddr(request));
		logger.info("User-Agent={}", request.getHeader(Const.USER_AGENT));
		ProcessVar.setProcessId(request.getSession().getId());
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

}
