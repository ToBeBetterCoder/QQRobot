package org.cool.qqrobot.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.cool.qqrobot.common.AutoReplySetting;
import org.cool.qqrobot.common.CacheMap;
import org.cool.qqrobot.common.Const;
import org.cool.qqrobot.dto.RobotResult;
import org.cool.qqrobot.entity.AutoReply;
import org.cool.qqrobot.entity.ProcessData;
import org.cool.qqrobot.exception.RobotException;
import org.cool.qqrobot.service.RobotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 主控制器，负责获取二维码登录以及登出等
 * @author zhoukl
 *
 */
@Controller
public class RobotController extends BaseController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RobotService robotService;
	
	@RequestMapping(value="/funnyRobot", method = RequestMethod.GET)
	public String getCodeToLogin(HttpSession session, Model model) {
		ProcessData processDataSession = (ProcessData) session.getAttribute(Const.PROCESS_DATA);
		if (null != processDataSession && CacheMap.isOnline(processDataSession)) {
			// 登陆后可以设置是否自动回复，是否自定义回复名单
			AutoReply autoReply = processDataSession.getAutoReply();
			Map<String, Object> friendsViewMap = robotService.buildFriendsList(processDataSession);
			Map<String, Object> discussesViewMap = robotService.buildDiscussesList(processDataSession);
			Map<String, Object> groupsViewMap = robotService.buildGroupsList(processDataSession);
			
			model.addAttribute("autoReply", autoReply);
			model.addAttribute("friendsViewMap", friendsViewMap);
			model.addAttribute("discussesViewMap", discussesViewMap);
			model.addAttribute("groupsViewMap", groupsViewMap);
			return "settings";
		} if (null != processDataSession && CacheMap.hasGetCode(processDataSession)) {
			// 如果已经扫描授权，则显示登录中
			model.addAttribute("isCodeScanned", processDataSession.isCodeScanned());
			// 防止刷新重复获取二维码
			model.addAttribute("imageCode", processDataSession.getImageCode());
			return "login";
		} else {
			ProcessData processData = new ProcessData();
			session.setAttribute(Const.PROCESS_DATA, processData);
			robotService.sessionSetter(session);
			String imageCode = robotService.getCode(processData);
			model.addAttribute("imageCode", imageCode);
			return "login";
		}
	}
	
	@RequestMapping(value="/submitList", method = RequestMethod.POST)
	@ResponseBody
	public RobotResult<Map<String, Object>> submitList(@RequestBody Map<String, Object> paramMap, HttpSession session) {
		ProcessData processDataSession = (ProcessData) session.getAttribute(Const.PROCESS_DATA);
		try {
			robotService.updateReplyNameList(paramMap, processDataSession);
			return successResult();
		} catch (RobotException e) {
			return exceptionResult();
		}
		
	}
	
	@RequestMapping(value="/setAutoReply", method = RequestMethod.POST)
	@ResponseBody
	public RobotResult<Map<String, Object>> setAutoReply(@RequestBody Map<String, Object> paramMap, HttpSession session) {
		ProcessData processDataSession = (ProcessData) session.getAttribute(Const.PROCESS_DATA);
		try {
			robotService.updateIsAutoReply(paramMap, processDataSession);
			return successResult();
		} catch (RobotException e) {
			return exceptionResult();
		}
	}

	@RequestMapping(value="/setReplyAll", method = RequestMethod.POST)
	@ResponseBody
	public RobotResult<Map<String, Object>> setReplyAll(@RequestBody Map<String, Object> paramMap, HttpSession session) {
		ProcessData processDataSession = (ProcessData) session.getAttribute(Const.PROCESS_DATA);
		try {
			robotService.updateIsSpecial(paramMap, processDataSession);
			return successResult();
		} catch (RobotException e) {
			return exceptionResult();
		}
	}
	
	/**
	 * 用户主动退出
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/robotQuit", method = RequestMethod.GET)
	@ResponseBody
	public RobotResult<Map<String, Object>> quit(HttpSession session) {
		ProcessData processDataSession = (ProcessData) session.getAttribute(Const.PROCESS_DATA);
		try {
			robotService.sessionSetter(session);
			robotService.quit(processDataSession);
			processDataSession = null;
			return successResult();
		} catch (RobotException e) {
			return exceptionResult();
		}
	}
	
	/**
	 * 踢出用户
	 * @param account
	 * @return
	 */
	@RequestMapping(value="/{account}/offline", method = RequestMethod.GET)
	@ResponseBody
	public RobotResult<Map<String, Object>> popUp(@PathVariable("account") String account) {
		try {
			robotService.popUp(account);
			return successResult();
		} catch (RobotException e) {
			return exceptionResult();
		}
	}
	
	/**
	 * 统计轮询消息线程数
	 * @return
	 */
	@RequestMapping(value="/onlineCount", method = RequestMethod.GET)
	@ResponseBody
	public RobotResult<Map<String, Object>> onlineCount() {
		try {
			Map<String, Object> responseMap = new HashMap<String, Object>();
			responseMap.put(Const.COUNT, CacheMap.threadFuturMap.size());
			return successResult(responseMap);
		} catch (RobotException e) {
			return exceptionResult();
		}
	}
	
	@RequestMapping(value="/{paramkey}/{paramValue}/defaultParamSet", method = RequestMethod.GET)
	@ResponseBody
	public RobotResult<Map<String, Object>> defaultParamSet(@PathVariable("paramkey") String paramkey, @PathVariable("paramValue") String paramValue) {
		try {
			if (paramkey.equals("autoReply")) {
				if (paramValue.equals("1")) {
					AutoReplySetting.autoReply = true;
				}
				if (paramValue.equals("0")) {
					AutoReplySetting.autoReply = false;
				}
			}
			if (paramkey.equals("special")) {
				if (paramValue.equals("1")) {
					AutoReplySetting.special = true;
				}
				if (paramValue.equals("0")) {
					AutoReplySetting.special = false;
				}
			}
			return successResult();
		} catch (Exception e) {
			logger.error("默认参数设置异常", e);
			return exceptionResult();
		}
	}
}
