package org.cool.qqrobot.web;

import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.cool.qqrobot.common.Const;
import org.cool.qqrobot.entity.MyHttpResponse;
import org.cool.qqrobot.entity.ProcessData;
import org.cool.qqrobot.http.MyHttpClient;
import org.cool.qqrobot.service.RobotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
/**
 * 主控制器，负责获取二维码登录以及登出等
 * @author zhoukl
 *
 */
@Controller
@RequestMapping("/robot")
public class RobotController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RobotService robotService;
	
	@RequestMapping(value="/getCodeToLogin", method = RequestMethod.GET)
	public String getCodeToLogin(HttpSession session, Model model) {
		// TODO: 当用户session失效后，用户依然可以重复登陆，所以在用户首次登录成功后，以用户QQ号为标识，在redis中记录用户的登录状态，每次用户登录成功时，判断是否已经登录了，如果之前已经登录，则不进行消息获取。
		// 通过session判断 防止同一用户多次刷新获取二维码
		Object obj = session.getAttribute(Const.PROCESS_DATA);
		if (obj instanceof ProcessData && ((ProcessData) obj).isLogin()) {
			model.addAttribute("imageCode", ((ProcessData) obj).getImageCode());
		} else {
			ProcessData processData = new ProcessData();
			session.setAttribute(Const.PROCESS_DATA, processData);
			MyHttpResponse getCodeResponse = robotService.getCode(processData);
			String imageCode = null;
			if (MyHttpResponse.S_OK == getCodeResponse.getStatus()) {
				imageCode = "data:" + getCodeResponse.getContentType() + ";base64," 
						+ Base64.encodeBase64String(getCodeResponse.getImageCode());
				model.addAttribute("imageCode", imageCode);
				processData.setImageCode(imageCode);
				// 设置登录成功标志，防止用户多次刷新页面，启动多个线程，造成浪费（理论上应该在登录成功后设置标识，这里过早，所以后面如果发生异常未登录成功，再捕获异常，取消登录成功标志，最终登录成功后再设置为成功）
				processData.setLogin(true);
			}
		}
		return "login";
	}
}
