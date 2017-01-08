package org.cool.qqrobot.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.cool.qqrobot.common.Const;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
@Component
//@Lazy(false)
public class DataEngine implements ServletContextAware {
	private ServletContext servletContext;
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	@PostConstruct
	public void contextInit() {
		setStaticResVer();
	}
	/**
	 * 设置静态资源版本号
	 */
	private void setStaticResVer() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(Const.DATE_Y_M_D_H_M_S);
		String value = dateFormat.format(calendar.getTime());
		this.servletContext.setAttribute(Const.RES_VERSION, value);
	}
}
