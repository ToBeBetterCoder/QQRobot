package org.cool.qqrobot.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.MapUtils;
import org.cool.qqrobot.common.Const;
import org.cool.qqrobot.common.RobotCodeEnums;
import org.cool.qqrobot.dto.RobotResult;
import org.cool.qqrobot.entity.AutoReply;
import org.cool.qqrobot.entity.MyHttpResponse;
import org.cool.qqrobot.entity.ProcessData;
import org.cool.qqrobot.exception.RobotException;
import org.cool.qqrobot.service.RobotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
@RequestMapping("/robot")
public class RobotController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RobotService robotService;
	
	@RequestMapping(value="/getCodeToLogin", method = RequestMethod.GET)
	public String getCodeToLogin(HttpSession session, Model model) {
		// 通过session判断 防止同一用户多次刷新获取二维码
		ProcessData processDataSession = (ProcessData) session.getAttribute(Const.PROCESS_DATA);
		if (null != processDataSession && processDataSession.isLogin()) {
			// 登陆后可以设置是否自动回复，是否自定义回复名单
			AutoReply autoReply = processDataSession.getAutoReply();
			Map<String, Object> friendsViewMap = processDataSession.getFriendsViewMap();
			if (!Const.SUCCESS_CODE.equals(MapUtils.getInteger(friendsViewMap, Const.RET_CODE))) {
				friendsViewMap = robotService.buildFriendsList(processDataSession);
			}
			Map<String, Object> discussesViewMap = processDataSession.getDiscussesViewMap();
			if (!Const.SUCCESS_CODE.equals(MapUtils.getInteger(discussesViewMap, Const.RET_CODE))) {
				discussesViewMap = robotService.buildDiscussesList(processDataSession);
			}
			Map<String, Object> groupsViewMap = processDataSession.getGroupsViewMap();
			if (!Const.SUCCESS_CODE.equals(MapUtils.getInteger(groupsViewMap, Const.RET_CODE))) {
				groupsViewMap = robotService.buildGroupsList(processDataSession);
			}
			model.addAttribute("autoReply", autoReply);
			model.addAttribute("friendsViewMap", friendsViewMap);
			model.addAttribute("discussesViewMap", discussesViewMap);
			model.addAttribute("groupsViewMap", groupsViewMap);
			return "settings";
		} if (null != processDataSession && processDataSession.isGetCode()) {
			// 防止刷新重复获取二维码
			model.addAttribute("imageCode", processDataSession.getImageCode());
			return "login";
		} else {
			ProcessData processData = new ProcessData();
			session.setAttribute(Const.PROCESS_DATA, processData);
			robotService.sessionSetter(session);
			MyHttpResponse getCodeResponse = robotService.getCode(processData);
			String imageCode = null;
			if (MyHttpResponse.S_OK == getCodeResponse.getStatus()) {
				imageCode = "data:" + getCodeResponse.getContentType() + ";base64," 
						+ Base64.encodeBase64String(getCodeResponse.getImageCode());
				model.addAttribute("imageCode", imageCode);
				processData.setImageCode(imageCode);
				// 设置二维码获取成功标识
				processData.setGetCode(true);
			}
			return "login";
		}
	}
	
	@RequestMapping(value="/submitList", method = RequestMethod.POST)
	@ResponseBody
	public RobotResult<Map<String, Object>> submitList(@RequestBody Map<String, Object> paramMap, HttpSession session) {
		ProcessData processDataSession = (ProcessData) session.getAttribute(Const.PROCESS_DATA);
		if (null == processDataSession) {
			return new RobotResult<Map<String, Object>>(true, RobotCodeEnums.SESSION_EXPIRED.getCode(), RobotCodeEnums.SESSION_EXPIRED.getCodeInfo());
		}
		try {
			Map<String, Object> responseMap = new HashMap<String, Object>();
			responseMap.put("info", RobotCodeEnums.LIST_SUBMIT_SUCCESS.getCodeInfo());
			robotService.updateReplyNameList(paramMap, processDataSession);
			return new RobotResult<Map<String, Object>>(true, RobotCodeEnums.LIST_SUBMIT_SUCCESS.getCode(), responseMap);
		} catch (RobotException e) {
			return new RobotResult<Map<String, Object>>(true, RobotCodeEnums.LIST_SUBMIT_FAIL.getCode(), RobotCodeEnums.LIST_SUBMIT_FAIL.getCodeInfo());
		}
		
	}
}
