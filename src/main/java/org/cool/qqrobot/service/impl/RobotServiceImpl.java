package org.cool.qqrobot.service.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.cool.qqrobot.common.CacheMap;
import org.cool.qqrobot.common.Const;
import org.cool.qqrobot.common.RobotCodeEnums;
import org.cool.qqrobot.dao.RobotDao;
import org.cool.qqrobot.dao.cache.RedisDao;
import org.cool.qqrobot.entity.AutoReply;
import org.cool.qqrobot.entity.MyHttpRequest;
import org.cool.qqrobot.entity.MyHttpResponse;
import org.cool.qqrobot.entity.ProcessData;
import org.cool.qqrobot.entity.ReplyName;
import org.cool.qqrobot.entity.UserInfo;
import org.cool.qqrobot.exception.RobotException;
import org.cool.qqrobot.service.RobotService;
import org.cool.qqrobot.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RobotServiceImpl implements RobotService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private HttpSession session = null;
	@Autowired
	private RobotDao robotDao;
	@Autowired
	private RedisDao redisDao;
	@Override
	public String getCode(ProcessData processData) {
		String imageCode = null;
		MyHttpRequest codeRequest = new MyHttpRequest();
		codeRequest.setUrl("https://ssl.ptlogin2.qq.com/ptqrshow?appid=501004106&e=0&l=M&s=5&d=72&v=4&t=" + Math.random());
		MyHttpResponse codeResponse = new MyHttpResponse();
		try {
			codeResponse = processData.getMyHttpClient().execute(codeRequest);
		} catch (Exception e) {
			logger.error("二维码获取异常", e);
		}
		if (MyHttpResponse.S_OK == codeResponse.getStatus()) {
			imageCode = "data:" + codeResponse.getContentType() + ";base64," 
					+ Base64.encodeBase64String(codeResponse.getImageCode());
			processData.setImageCode(imageCode);
			// 设置二维码获取成功标识
			processData.setGetCode(true);
			loginCheck(processData);
		}
		return imageCode;
	}
	
	private void loginCheck(ProcessData processData) {
		ThreadPool.getInstance().getFixedThreadPool().execute(new Runnable() {
			
			@Override
			public void run() {
				for (int i = 0; i < Const.CYCLE_NUM; i++) {
					MyHttpRequest checkRequest = new MyHttpRequest();
					checkRequest.setUrl("https://ssl.ptlogin2.qq.com/ptqrlogin?webqq_type=10&remember_uin=1&login2qq=1&aid=501004106&u1=http%3A%2F%2Fw.qq.com%2Fproxy.html%3Flogin2qq%3D1%26webqq_type%3D10&ptredirect=0&ptlang=2052&daid=164&from_ui=1&pttype=1&dumy=&fp=loginerroralert&action=0-0-9604&mibao_css=m_webqq&t=undefined&g=1&js_type=0&js_ver=10181&login_sig=&pt_randsalt=0");
					MyHttpResponse checkResponse = new MyHttpResponse();
					try {
						checkResponse = processData.getMyHttpClient().execute(checkRequest);
					} catch (Exception e) {
						processData.setGetCode(false);
						logger.error("二维码登录状态获取异常", e);
					}
					boolean firstLoginSuccess = false;
					if (MyHttpResponse.S_OK == checkResponse.getStatus()) {
						firstLoginSuccess = firstLogin(processData, checkResponse);
					} else {
						processData.setGetCode(false);
					}
					threadSleep();
					if (firstLoginSuccess) {
						break;
					}
					// 轮询时间结束，用户还未扫描登录，自动标识为登录失败（不然下次不能获取二维码，刷新页面二维码不变）
					if (i == Const.CYCLE_NUM - 1) {
						processData.setGetCode(false);
					}
				}
			}
		});
	}
	
	private boolean firstLogin(ProcessData processData, MyHttpResponse checkResponse) {
		String[] checkResponseArr = checkResponse.getTextStr().split(",");
		if (checkResponseArr[0].contains(Const.SUCCESS_CODE.toString())) {
			// 二维码扫描成功
			processData.setCodeScanned(true);
			MyHttpRequest firstLoginRequest = new MyHttpRequest();
			firstLoginRequest.setUrl(checkResponseArr[2].replaceAll("'", ""));
			MyHttpResponse firstLoginResponse = new MyHttpResponse();
			try {
				firstLoginResponse = processData.getMyHttpClient().execute(firstLoginRequest);
			} catch (Exception e) {
				processData.setGetCode(false);
				logger.error("first登陆异常", e);
			}
			if (MyHttpResponse.S_OK == firstLoginResponse.getStatus()) {
				processData.setPtwebqq(firstLoginResponse.getCookiesValue(Const.PTWEBQQ));
				getVfwebqq(processData);
				return true;
			} else {
				processData.setGetCode(false);
			}
		} else if (checkResponseArr[0].contains(Const.INVALID_CODE.toString())) {
			// 二维码已失效
			processData.setGetCode(false);
			return true;
		}
		return false;
	}

	private void getVfwebqq(ProcessData processData) {
		MyHttpRequest vfwebqqRequest = new MyHttpRequest();
		vfwebqqRequest.getHeaderMap().put(Const.REFERER, Const.REFERER_S);
		vfwebqqRequest.setUrl("http://s.web2.qq.com/api/getvfwebqq?clientid=53999199");
		MyHttpResponse vfwebqqResponse = new MyHttpResponse();
		try {
			vfwebqqResponse = processData.getMyHttpClient().execute(vfwebqqRequest);
		} catch (Exception e) {
			processData.setGetCode(false);
			logger.error("vfwebqq获取异常", e);
		}
		if (MyHttpResponse.S_OK == vfwebqqResponse.getStatus()) {
			if (Const.SUCCESS_CODE.equals(MapUtils.getInteger(vfwebqqResponse.getJsonMap(), Const.RET_CODE))) {
				processData.setVfwebqq(MapUtils.getString(MapUtils.getMap(vfwebqqResponse.getJsonMap(), Const.RESULT), Const.VFWEBQQ));
				secondLogin(processData);
			} else {
				processData.setGetCode(false);
			}
		} else {
			processData.setGetCode(false);
		}
	}

	private void secondLogin(ProcessData processData) {
		MyHttpRequest secondLoginRequest = new MyHttpRequest(HttpPost.METHOD_NAME);
		secondLoginRequest.setUrl("http://d1.web2.qq.com/channel/login2");
		secondLoginRequest.getPostMap().put(Const.R, "{\"ptwebqq\":\"" + processData.getPtwebqq() + "\",\"clientid\":53999199,\"psessionid\":\"\",\"status\":\"online\"}");
		MyHttpResponse secondLoginResponse = new MyHttpResponse();
		try {
			secondLoginResponse = processData.getMyHttpClient().execute(secondLoginRequest);
		} catch (Exception e) {
			processData.setGetCode(false);
			logger.error("second登录异常", e);
		}
		if (MyHttpResponse.S_OK == secondLoginResponse.getStatus()) {
			if (Const.SUCCESS_CODE.equals(MapUtils.getInteger(secondLoginResponse.getJsonMap(), Const.RET_CODE))) {
				processData.setSelfUiu(MapUtils.getObject(MapUtils.getMap(secondLoginResponse.getJsonMap(), Const.RESULT), Const.UIN));
				processData.setPsessionid(MapUtils.getString(MapUtils.getMap(secondLoginResponse.getJsonMap(), Const.RESULT), Const.P_SESSION_ID));
				ProcessData dataFromCache = CacheMap.processDataMap.get(processData.getSelfUiu());
				// 防止重复登陆，当用户session失效后，用户依然可以重复登陆，所以在用户首次登录成功后，以用户QQ号为标识，在CacheMap中记录用户的processData，每次用户登录成功时，判断是否已经登录了，如果之前已经登录，则不进行消息获取。
				if (null == dataFromCache || !dataFromCache.isLogin() || !CacheMap.isThreadAlive(dataFromCache)) {
					// 获取好友列表、群列表、讨论组列表、个人信息（异步）
					multipleInfoGet(processData);
					// 获取在线好友  否则如果不先登录webbQQ会报：{"errmsg":"error!!!","retcode":103} 无法获取消息和发送消息
					if (onlineBuddies(processData)) {
						// 设置允许自动回复的uin（Tomcat首次启动，数据库经常获取连接失败，现在增加一次获取）
						try {
							setAutoReply(processData);
						} catch (Exception e) {
							logger.error("获取自动回复列表异常", e);
							try {
								threadSleep();
								logger.debug("重新获取自动回复列表");
								setAutoReply(processData);
							} catch (Exception e1) {
								processData.setGetCode(false);
								logger.error("重新获取自动回复列表异常", e1);
								return;
							}
						}
						// 异步轮询获取消息 webQQ轮询获取消息机制：客户端发起一次poll请求,服务端进行轮询，1分钟没有消息返回，则返回{"errmsg":"error!!!","retcode":0}
						pollMessageThread(processData);
						// 设置最终登录成功标志
						processData.setLogin(true);
						// 更新缓存信息
						CacheMap.processDataMap.put(processData.getSelfUiu(), processData);
					}
					
				} else {
					session.setAttribute(Const.PROCESS_DATA, dataFromCache);
				}
			} else {
				processData.setGetCode(false);
			}
		} else {
			processData.setGetCode(false);
		}
	}

	private void setAutoReply(ProcessData processData) throws Exception {
		if (0 == robotDao.hasAutoReply(processData.getSelfUiu())) {
			AutoReply autoReply = new AutoReply(processData.getSelfUiu());
			robotDao.initAutoReply(autoReply.getAccount(), autoReply.getIsAutoReply() ? 1 : 0, autoReply.getIsSpecial() ? 1 : 0);
		}
		processData.setAutoReply(robotDao.queryAutoReplyNames(processData.getSelfUiu()));
	}

	private void multipleInfoGet(ProcessData processData) {
		// 获取联系人列表经常返回异常，现在增加重试措施
		if (!friendsList(processData)) {
			threadSleep();
			logger.debug("重新获取好友列表");
			friendsList(processData);
		}
		if (!groupsList(processData)) {
			threadSleep();
			logger.debug("重新获取群列表");
			groupsList(processData);
		}
		if (!discussesList(processData)) {
			threadSleep();
			logger.debug("重新获取讨论组列表");
			discussesList(processData);
		}
		ThreadPool.getInstance().getFixedThreadPool().execute(new Runnable() {
			
			@Override
			public void run() {
				selfInfo(processData);
			}
		});
	}

	private void pollMessageThread(ProcessData processData) {
		// 把每一个轮询线程对象保存到map中，便于终止轮询（key:qq号，value:Future对象），同时清除登录成功的缓存信息，更新退出时间
		Future<?> future = ThreadPool.getInstance().getScheduledThreadPool().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				MyHttpRequest pollMessageRequest = new MyHttpRequest(HttpPost.METHOD_NAME);
				pollMessageRequest.getHeaderMap().put(Const.REFERER, Const.REFERER_D_S);
				pollMessageRequest.setUrl("https://d1.web2.qq.com/channel/poll2");
				pollMessageRequest.getPostMap().put(Const.R, "{\"ptwebqq\":\"" + processData.getPtwebqq() + "\",\"clientid\":53999199,\"psessionid\":\"" + processData.getPsessionid() + "\",\"key\":\"\"}");
				MyHttpResponse pollMessageResponse = new MyHttpResponse();
				try {
					pollMessageResponse = processData.getMyHttpClient().execute(pollMessageRequest);
				} catch (Exception e) {
					processData.setGetCode(false);
					logger.error("poolMessage异常", e);
				}
				if (MyHttpResponse.S_OK == pollMessageResponse.getStatus()) {
					if (Const.SUCCESS_CODE.equals(MapUtils.getInteger(pollMessageResponse.getJsonMap(), Const.RET_CODE))) {
						Map<String, Object> resultMap = ((List<Map<String, Object>>) MapUtils.getObject(pollMessageResponse.getJsonMap(), Const.RESULT)).get(0);
						Map<String, Object> valueMap = MapUtils.getMap(resultMap, Const.VALUE);
						// 消息来源类型（私人、讨论组、群）
						String pollType = MapUtils.getString(resultMap, Const.POLL_TYPE);
						// 发送方uin
						String fromUin = new DecimalFormat("#").format(MapUtils.getObject(valueMap, Const.FROM_UIN));
						// 是否自动回复
						if (!autoReply(processData, fromUin)) {
							return;
						}
						// 空字符不去调机器人API 也不回复
						String requestContent = requestContent((List<Object>) MapUtils.getObject(valueMap, Const.CONTENT));
						if (StringUtils.isBlank(requestContent)) {
							return;
						}
						// 返回空不回复
						String responseContent = tuRingRobot(processData, fromUin, requestContent);
						if (StringUtils.isBlank(responseContent)) {
							return;
						}
						// 回复消息
						sendMessage(processData, pollType, fromUin, responseContent);
					}
				}
			}
		}, Const.INIT_DELAY, Const.PERIOD, TimeUnit.MICROSECONDS);
		// 保存当前线程future
		CacheMap.threadFuturMap.put(processData.getSelfUiu(), future);
	}
	
	private boolean autoReply(ProcessData processData, String fromUin) {
		boolean isReply = false;
		// 自动回复关闭，则不回复
		if (null == processData.getAutoReply() || !processData.getAutoReply().getIsAutoReply()) {
			return isReply;
		}
		if (processData.getAutoReply().getIsSpecial()) {
			List<ReplyName> replyNameList = processData.getAutoReply().getReplyNameList();
			for (ReplyName replyName : replyNameList) {
				if (fromUin.equals(replyName.getUin())) {
					isReply = true;
					break;
				}
			}
		} else {
			isReply = true;
		}
		return isReply;
	}
	
	// 过滤@开头的 过滤空格 过滤换行\n 过滤表情
	private String requestContent(List<Object> contentList) {
		StringBuffer requestContentBuffer = new StringBuffer("");
		// 下标0是字体样式，不涉及内容
		for (int i = 1; i < contentList.size(); i++) {
			Object content = contentList.get(i);
			if (content instanceof String && StringUtils.isNotBlank((String) content) && !((String) content).startsWith(Const.AT)) {
				requestContentBuffer.append(StringUtils.trim(((String) content)).replaceAll(Const.R_NEW_LINE, ""));
			}
		}
		return requestContentBuffer.toString();
	}
	
	// http调用图灵机器人API
	private String tuRingRobot(ProcessData processData, String fromUin, String requestContent) {
		StringBuffer responseContentBuffer = new StringBuffer("");
		MyHttpRequest tuRingRobotRequest = new MyHttpRequest(HttpPost.METHOD_NAME);
		tuRingRobotRequest.getHeaderMap().put(Const.CONTENT_TYPE, Const.CONTENT_TYPE_JSON);
		tuRingRobotRequest.setUrl("http://www.tuling123.com/openapi/api");
		tuRingRobotRequest.setParamString("{\"key\":\"" + Const.API_KEY + "\", \"info\": \"" + requestContent + "\", \"userid\": \"" + fromUin + "\"}");
		MyHttpResponse tuRingRobotResponse = new MyHttpResponse();
		try {
			tuRingRobotResponse = processData.getMyHttpClient().execute(tuRingRobotRequest);
		} catch (Exception e) {
			logger.error("调用图灵机器人API异常", e);
		}
		if (MyHttpResponse.S_OK == tuRingRobotResponse.getStatus()) {
			Map<String, Object> responseMap = tuRingRobotResponse.getJsonMap();
			if (RobotCodeEnums.TEXT.getCode() == MapUtils.getIntValue(responseMap, Const.CODE)) {
				responseContentBuffer.append(responseMap.get(Const.TEXT));
			} else if (RobotCodeEnums.LINK.getCode() == MapUtils.getIntValue(responseMap, Const.CODE)) {
				responseContentBuffer.append(responseMap.get(Const.TEXT));
				responseContentBuffer.append(Const.NEW_LINE);
				responseContentBuffer.append(responseMap.get(Const.URL));
			} else if (RobotCodeEnums.NEW.getCode() == MapUtils.getIntValue(responseMap, Const.CODE)) {
				responseContentBuffer.append(responseMap.get(Const.TEXT));
				responseContentBuffer.append(Const.NEW_LINE);
				List<Map<String, String>> newList = (List<Map<String, String>>) responseMap.get(Const.LIST);
				for (int i = 0; i < (newList.size() > Const.NEW_MAX_NUM ? Const.NEW_MAX_NUM : newList.size()); i++) {
					responseContentBuffer.append(Const.NEW_LINE);
					responseContentBuffer.append("【" + MapUtils.getString(newList.get(i), Const.SOURCE) + "】" + MapUtils.getString(newList.get(i), Const.ARTICLE));
					responseContentBuffer.append(Const.NEW_LINE);
					responseContentBuffer.append(MapUtils.getString(newList.get(i), Const.DETAIL_URL));
					responseContentBuffer.append(Const.NEW_LINE);
				}
			} else if (RobotCodeEnums.COOK.getCode() == MapUtils.getIntValue(responseMap, Const.CODE)) {
				responseContentBuffer.append(responseMap.get(Const.TEXT));
				responseContentBuffer.append(Const.NEW_LINE);
				List<Map<String, String>> cookList = (List<Map<String, String>>) responseMap.get(Const.LIST);
				for (int i = 0; i < (cookList.size() > Const.COOK_MAX_NUM ? Const.COOK_MAX_NUM : cookList.size()); i++) {
					responseContentBuffer.append(Const.NEW_LINE);
					responseContentBuffer.append("【" + MapUtils.getString(cookList.get(i), Const.NAME) + "】");
					responseContentBuffer.append(Const.NEW_LINE);
					responseContentBuffer.append(MapUtils.getString(cookList.get(i), Const.INFO));
					responseContentBuffer.append(Const.NEW_LINE);
					responseContentBuffer.append(MapUtils.getString(cookList.get(i), Const.DETAIL_URL));
					responseContentBuffer.append(Const.NEW_LINE);
				}
			} else {
				// 返回异常编码
				logger.debug(RobotCodeEnums.stateOf(MapUtils.getIntValue(responseMap, Const.CODE)).getCodeInfo() + ":{}", MapUtils.getString(responseMap, Const.TEXT));
			}
		}
		return responseContentBuffer.toString();
	}
	
	// 自动回复消息
	private void sendMessage(ProcessData processData, String pollType, String fromUin, String responseContent) {
		MyHttpRequest sendMessageRequest = new MyHttpRequest(HttpPost.METHOD_NAME);
		sendMessageRequest.getHeaderMap().put(Const.REFERER, Const.REFERER_D_S);
		if (Const.MESSAGE.equals(pollType)) {
			sendMessageRequest.setUrl("https://d1.web2.qq.com/channel/send_buddy_msg2");
			sendMessageRequest.getPostMap().put(Const.R, "{\"to\":" + fromUin + ",\"content\":\"[\\\"" + responseContent + "\\\",[\\\"font\\\",{\\\"name\\\":\\\"宋体\\\",\\\"size\\\":10,\\\"style\\\":[0,0,0],\\\"color\\\":\\\"000000\\\"}]]\",\"face\":480,\"clientid\":53999199,\"msg_id\":37040006,\"psessionid\":\"" + processData.getPsessionid() + "\"}");
		}
		if (Const.DISCU_MESSAGE.equals(pollType)) {
			sendMessageRequest.setUrl("https://d1.web2.qq.com/channel/send_discu_msg2");
			sendMessageRequest.getPostMap().put(Const.R, "{\"did\":" + fromUin + ",\"content\":\"[\\\"" + responseContent + "\\\",[\\\"font\\\",{\\\"name\\\":\\\"宋体\\\",\\\"size\\\":10,\\\"style\\\":[0,0,0],\\\"color\\\":\\\"000000\\\"}]]\",\"face\":480,\"clientid\":53999199,\"msg_id\":37040006,\"psessionid\":\"" + processData.getPsessionid() + "\"}");
		}
		if (Const.GROUP_MESSAGE.equals(pollType)) {
			sendMessageRequest.setUrl("https://d1.web2.qq.com/channel/send_qun_msg2");
			sendMessageRequest.getPostMap().put(Const.R, "{\"group_uin\":" + fromUin + ",\"content\":\"[\\\"" + responseContent + "\\\",[\\\"font\\\",{\\\"name\\\":\\\"宋体\\\",\\\"size\\\":10,\\\"style\\\":[0,0,0],\\\"color\\\":\\\"000000\\\"}]]\",\"face\":480,\"clientid\":53999199,\"msg_id\":37040006,\"psessionid\":\"" + processData.getPsessionid() + "\"}");
		}
		MyHttpResponse sendMessageResponse = new MyHttpResponse();
		try {
			sendMessageResponse = processData.getMyHttpClient().execute(sendMessageRequest);
		} catch (Exception e) {
			logger.error("sendMessage异常", e);
		}
		if (MyHttpResponse.S_OK == sendMessageResponse.getStatus()) {
			if (!Const.SUCCESS_CODE.equals(MapUtils.getInteger(sendMessageResponse.getJsonMap(), Const.ERR_CODE))) {
				logger.debug("sendMessage异常");
			}
		}
	}
	
	private boolean friendsList(ProcessData processData) {
		MyHttpRequest friendsRequest = new MyHttpRequest(HttpPost.METHOD_NAME);
		friendsRequest.getHeaderMap().put(Const.REFERER, Const.REFERER_S);
		friendsRequest.setUrl("http://s.web2.qq.com/api/get_user_friends2");
		friendsRequest.getPostMap().put(Const.R, "{\"vfwebqq\":\"" + processData.getVfwebqq() + "\",\"hash\":\"" + hashByJs(processData.getSelfUiu(), processData.getPtwebqq()) + "\"}");
		MyHttpResponse friendsResponse = new MyHttpResponse();
		try {
			friendsResponse = processData.getMyHttpClient().execute(friendsRequest);
		} catch (Exception e) {
			processData.setGetCode(false);
			logger.error("获取好友列表异常", e);
		}
		if (MyHttpResponse.S_OK == friendsResponse.getStatus()) {
			if (Const.SUCCESS_CODE.equals(MapUtils.getInteger(friendsResponse.getJsonMap(), Const.RET_CODE))) {
				processData.setFriendsMap(MapUtils.getMap(friendsResponse.getJsonMap(), Const.RESULT));
				return true;
			} else {
				processData.setGetCode(false);
				logger.error("获取好友列表异常");
			}
		} else {
			logger.error("获取好友列表异常");
		}
		return false;
	}

	private boolean groupsList(ProcessData processData) {
		MyHttpRequest groupsRequest = new MyHttpRequest(HttpPost.METHOD_NAME);
		groupsRequest.getHeaderMap().put(Const.REFERER, Const.REFERER_S);
		groupsRequest.setUrl("http://s.web2.qq.com/api/get_group_name_list_mask2");
		groupsRequest.getPostMap().put(Const.R, "{\"vfwebqq\":\"" + processData.getVfwebqq() + "\",\"hash\":\"" + hashByJs(processData.getSelfUiu(), processData.getPtwebqq()) + "\"}");
		MyHttpResponse groupsResponse = new MyHttpResponse();
		try {
			groupsResponse = processData.getMyHttpClient().execute(groupsRequest);
		} catch (Exception e) {
			processData.setGetCode(false);
			logger.error("获取群列表异常", e);
		}
		if (MyHttpResponse.S_OK == groupsResponse.getStatus()) {
			if (Const.SUCCESS_CODE.equals(MapUtils.getInteger(groupsResponse.getJsonMap(), Const.RET_CODE))) {
				processData.setGroupsMap(MapUtils.getMap(groupsResponse.getJsonMap(), Const.RESULT));
				return true;
			} else {
				processData.setGetCode(false);
				logger.error("获取群列表异常");
			}
		} else {
			logger.error("获取群列表异常");
		}
		return false;
	}

	private boolean discussesList(ProcessData processData) {
		MyHttpRequest discussesRequest = new MyHttpRequest();
		discussesRequest.getHeaderMap().put(Const.REFERER, Const.REFERER_S);
		discussesRequest.setUrl("http://s.web2.qq.com/api/get_discus_list?clientid=53999199&psessionid=" + processData.getPsessionid() + "&vfwebqq=" + processData.getVfwebqq());
		MyHttpResponse discussesResponse = new MyHttpResponse();
		try {
			discussesResponse = processData.getMyHttpClient().execute(discussesRequest);
		} catch (Exception e) {
			processData.setGetCode(false);
			logger.error("获取讨论组列表异常", e);
		}
		if (MyHttpResponse.S_OK == discussesResponse.getStatus()) {
			if (Const.SUCCESS_CODE.equals(MapUtils.getInteger(discussesResponse.getJsonMap(), Const.RET_CODE))) {
				processData.setDiscussesMap(MapUtils.getMap(discussesResponse.getJsonMap(), Const.RESULT));
				return true;
			} else {
				processData.setGetCode(false);
				logger.error("获取讨论组列表异常");
			}
		} else {
			logger.error("获取讨论组列表异常");
		}
		return false;
	}

	private void selfInfo(ProcessData processData) {
		MyHttpRequest selfInfoRequest = new MyHttpRequest();
		selfInfoRequest.getHeaderMap().put(Const.REFERER, Const.REFERER_S);
		selfInfoRequest.setUrl("http://s.web2.qq.com/api/get_self_info2");
		MyHttpResponse selfInfoResponse = new MyHttpResponse();
		try {
			selfInfoResponse = processData.getMyHttpClient().execute(selfInfoRequest);
		} catch (Exception e) {
			processData.setGetCode(false);
			logger.error("获取个人信息异常", e);
		}
		if (MyHttpResponse.S_OK == selfInfoResponse.getStatus()) {
			if (Const.SUCCESS_CODE.equals(MapUtils.getInteger(selfInfoResponse.getJsonMap(), Const.RET_CODE))) {
				UserInfo userInfo = new UserInfo(selfInfoResponse.getJsonMap(), selfInfoResponse.getTextStr());
				processData.setUserInfo(userInfo);
				// 异步记录登录用户信息
				// 登录成功后，记录当前登录日志的id，当用户主动退出时，再次更新登出时间
				ThreadPool.getInstance().getFixedThreadPool().execute(new Runnable() {
					
					@Override
					public void run() {
						robotDao.addLoginInfo(userInfo);
					}
				});
			}
		}
	}

	private boolean onlineBuddies(ProcessData processData) {
		MyHttpRequest onlineBuddiesRequest = new MyHttpRequest();
		onlineBuddiesRequest.getHeaderMap().put(Const.REFERER, Const.REFERER_D);
		onlineBuddiesRequest.setUrl("http://d1.web2.qq.com/channel/get_online_buddies2?vfwebqq=" + processData.getVfwebqq() + "&clientid=53999199&psessionid=" + processData.getPsessionid());
		MyHttpResponse onlineBuddiesResponse = new MyHttpResponse();
		try {
			onlineBuddiesResponse = processData.getMyHttpClient().execute(onlineBuddiesRequest);
		} catch (Exception e) {
			processData.setGetCode(false);
			logger.error("获取在线好友列表异常", e);
		}
		if (MyHttpResponse.S_OK == onlineBuddiesResponse.getStatus()) {
			if (Const.SUCCESS_CODE.equals(MapUtils.getInteger(onlineBuddiesResponse.getJsonMap(), Const.RET_CODE))) {
				processData.setOnlineBuddiesList((List<Map<String, Object>>) MapUtils.getObject(onlineBuddiesResponse.getJsonMap(), Const.RESULT));
				return true;
			} else {
				processData.setGetCode(false);
			}
		} else {
			processData.setGetCode(false);
		}
		return false;
	}

	private String hashByJs(String selfUiu, String ptwebqq) {
		ScriptEngineManager manager = new ScriptEngineManager(); 
		ScriptEngine engine = manager.getEngineByName(Const.JAVA_SCRIPT);     
		// 读取js文件 
		String jsFileName = this.getClass().getClassLoader().getResource(Const.ROOT_PATH).getPath().replace(Const.CLASSES, Const.JS).substring(1) + Const.JS_FILE_NAME; 
		// linux环境下必须在文件路径名称前加"/"，不然找不到文件
		if (!System.getProperty(Const.OS_NAME).contains(Const.WINDOWS)) {
			jsFileName = File.separator + jsFileName;
		}
		logger.debug("jsFileName:{}", jsFileName);
		FileReader reader = null;
		try {
			reader = new FileReader(jsFileName);
			engine.eval(reader);   
			if(engine instanceof Invocable) {    
				Invocable invoke = (Invocable)engine;
				// 执行js函数
				return (String) invoke.invokeFunction(Const.JS_HASH, selfUiu, ptwebqq);    
			}  
		} catch (Exception e) {
			logger.error("js执行运算HASH异常", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}  
			}
		}
		return "";
	}

	/*
	 * 返回样例
	 * {
			result: [
		    	{
		        	 "cateName": "我的好友",
		        	 "cateIndex": 0,
		        	 "cateSort": 0,
		        	 "list": [
	        	          {"nick": "小猪", "markname": "珠珠"},
	        	          {"nick": "小王", "markname": "王尼玛"},
	        	          {"nick": "小吴", "markname": "吴莫愁"}
		        	  ]
		         },
		         {
		        	 "category": "同学",
		        	 "cateIndex": 1,
		        	 "cateSort": 2,
		        	 "list": [
	        	          {"nick": "小猪", "markname": "珠珠"},
	        	          {"nick": "小王", "markname": "王尼玛"},
	        	          {"nick": "小吴", "markname": "吴莫愁"}
		        	  ]
		         }
			],
			retcode: 0
		}*/
	@Override
	public Map<String, Object> buildFriendsList(ProcessData processData) {
		if (Const.SUCCESS_CODE.equals(MapUtils.getInteger(processData.getFriendsViewMap(), Const.RET_CODE))) {
			return processData.getFriendsViewMap();
		}
		int retcode = Const.SUCCESS_CODE;
		Map<String, Object> newFriendsMap = new HashMap<String, Object>();
		Map<String, Object> friendsMap = processData.getFriendsMap();
		List<ReplyName> replyNameList = getReplyNameList(processData);
		List<Map<String, Object>> categoryList = (List<Map<String, Object>>) MapUtils.getObject(friendsMap, Const.CATEGORIES, new ArrayList<Map<String, Object>>());
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		try {
			// Tips 之前采用双括弧初始化map，但是转化成json时为null 原因：通过Gson串行化为json，或者要串行化为xml时，类库中提供的方式，是无法串行化Hashset或者HashMap的子类的，从而导致串行化失败。
			// 添加默认分组信息
			Map<String, Object> cateMap = new HashMap<String, Object>();
			cateMap.put(Const.CATE_INDEX, 0);
			cateMap.put(Const.CATE_NAME, Const.CATE_NAME_VAL);
			cateMap.put(Const.CATE_SORT, 0);
			resultList.add(cateMap);
			// 组装分组信息
			for (Map<String, Object> map : categoryList) {
				cateMap = new HashMap<String, Object>();
				cateMap.put(Const.CATE_INDEX, MapUtils.getIntValue(map, Const.INDEX));
				cateMap.put(Const.CATE_NAME, MapUtils.getString(map, Const.NAME));
				cateMap.put(Const.CATE_SORT, MapUtils.getIntValue(map, Const.SORT));
				resultList.add(cateMap);
			}
			
			List<Map<String, Object>> friendsWithCatelist = (List<Map<String, Object>>) MapUtils.getObject(friendsMap, Const.FRIENDS, new ArrayList<Map<String, Object>>());
			List<Map<String, Object>> friendsWithNicklist = (List<Map<String, Object>>) MapUtils.getObject(friendsMap, Const.INFO,  new ArrayList<Map<String, Object>>());
			List<Map<String, Object>> friendsWithMarkNamelist = (List<Map<String, Object>>) MapUtils.getObject(friendsMap, Const.MARK_NAMES,  new ArrayList<Map<String, Object>>());
			
			// 装配uin、昵称和备注的好友列表
			List<Map<String, Object>> newFriendslist = new ArrayList<Map<String, Object>>();
			// 拷贝friendsWithNicklist中的nick和uin(为了不影响原数据)
			Map<String, Object> friendWithNickMap;
			for (Map<String, Object> map : friendsWithNicklist) {
				friendWithNickMap = new HashMap<String, Object>();
				friendWithNickMap.put(Const.NICK, MapUtils.getString(map, Const.NICK));
				friendWithNickMap.put(Const.UIN, MapUtils.getString(map, Const.UIN));
				newFriendslist.add(friendWithNickMap);
			}
			// 组装组别（利用两组有序的一一对应的下标uin做对比，减少比较次数 O(n)）
			int j = 0;
			for (int i = 0; i < friendsWithCatelist.size(); i++) {
				for (; j < newFriendslist.size(); ) {
					if (MapUtils.getString(friendsWithCatelist.get(i), Const.UIN).equals(MapUtils.getString(newFriendslist.get(j), Const.UIN))) {
						newFriendslist.get(j).put(Const.CATE, friendsWithCatelist.get(i).get(Const.CATEGORIES));
						j++;
					}
					break;
				}
			}
			// 组装备注（如果没有备注，则不存在markName属性）
			int k = 0;
			for (int i = 0; i < newFriendslist.size(); i++) {
				for (; k < friendsWithMarkNamelist.size(); ) {
					if (MapUtils.getString(newFriendslist.get(i), Const.UIN).equals(MapUtils.getString(friendsWithMarkNamelist.get(k), Const.UIN))) {
						newFriendslist.get(i).put(Const.MARK_NAME, MapUtils.getString(friendsWithMarkNamelist.get(k), Const.MARK_NAME));
						k++;
					}
					break;
				}
			}
			// 按照newFriendslist的组别cate排序，与resultList的组别顺序一致，提高组别分配效率，减少分配次数
			Collections.sort(newFriendslist, new Comparator<Map<String, Object>>() {
	
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					return MapUtils.getIntValue(o1, Const.CATE) - MapUtils.getIntValue(o2, Const.CATE);
				}
				
			});
			// 分配好友到各组别（只设置nick和markName）
			int l = 0;
			Map<String, Object> friendMap;
			for (int i = 0; i < resultList.size(); i++) {
				resultList.get(i).put(Const.LIST, new ArrayList<Map<String, Object>>());
				for (; l < newFriendslist.size(); ) {
					if (MapUtils.getIntValue(resultList.get(i), Const.CATE_INDEX) == MapUtils.getIntValue(newFriendslist.get(l), Const.CATE)) {
						friendMap = new HashMap<String, Object>();
						String nick = MapUtils.getString(newFriendslist.get(l), Const.NICK);
						String markName = MapUtils.getString(newFriendslist.get(l), Const.MARK_NAME);
						friendMap.put(Const.NICK, nick);
						friendMap.put(Const.MARK_NAME, markName);
						// 设置好友被选标识
						if (replyNameList != null) {
							for (ReplyName replyName : replyNameList) {
								if (Const.PERSON == replyName.getType() && (markName == null ? nick : markName).equals(replyName.getMarkName())) {
									friendMap.put(Const.SELECT, Const.Y);
								}
							}
						}
						((List<Map<String, Object>>) MapUtils.getObject(resultList.get(i), Const.LIST)).add(friendMap);
						l++;
					} else {
						break;
					}
				}
			}
			// 排序组别
			Collections.sort(resultList, new Comparator<Map<String, Object>>() {
	
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					return MapUtils.getIntValue(o1, Const.CATE_SORT) - MapUtils.getIntValue(o2, Const.CATE_SORT);
				}
			});
		} catch (Exception e) {
			retcode = Const.EXCEPTION_CODE;
			logger.error("好友列表构建异常", e);
		}
		newFriendsMap.put(Const.RESULT, resultList);
		newFriendsMap.put(Const.RET_CODE, retcode);
		// 放入过程数据，不用每次都重新构建
		processData.setFriendsViewMap(newFriendsMap);
		return newFriendsMap;
	}

	@Override
	public Map<String, Object> buildDiscussesList(ProcessData processData) {
		if (Const.SUCCESS_CODE.equals(MapUtils.getInteger(processData.getDiscussesViewMap(), Const.RET_CODE))) {
			return processData.getDiscussesViewMap();
		}
		int retcode = Const.SUCCESS_CODE;
		Map<String, Object> newDiscussesMap = new HashMap<String, Object>();
		Map<String, Object> discussesMap = processData.getDiscussesMap();
		List<ReplyName> replyNameList = getReplyNameList(processData);
		List<Map<String, Object>> discussesList = (List<Map<String, Object>>) MapUtils.getObject(discussesMap, Const.D_NAME_LIST, new ArrayList<Map<String, Object>>());
		List<Map<String, Object>> newDiscussesList = new ArrayList<Map<String, Object>>();
		try {
			Map<String, Object> discussMap;
			for (Map<String, Object> map : discussesList) {
				discussMap = new HashMap<String, Object>();
				String discussName = MapUtils.getString(map, Const.NAME);
				discussMap.put(Const.NAME, discussName);
				if (replyNameList != null) {
					for (ReplyName replyName : replyNameList) {
						if (Const.DISCU == replyName.getType() && discussName.equals(replyName.getMarkName())) {
							discussMap.put(Const.SELECT, Const.Y);
						}
					}
				}
				newDiscussesList.add(discussMap);
			}
		} catch (Exception e) {
			retcode = Const.EXCEPTION_CODE;
			logger.error("讨论组列表构建异常", e);
		}
		newDiscussesMap.put(Const.RESULT, newDiscussesList);
		newDiscussesMap.put(Const.RET_CODE, retcode);
		processData.setDiscussesViewMap(newDiscussesMap);
		return newDiscussesMap;
	}

	@Override
	public Map<String, Object> buildGroupsList(ProcessData processData) {
		if (Const.SUCCESS_CODE.equals(MapUtils.getInteger(processData.getGroupsViewMap(), Const.RET_CODE))) {
			return processData.getGroupsViewMap();
		}
		int retcode = Const.SUCCESS_CODE;
		Map<String, Object> newGroupsMap = new HashMap<String, Object>();
		Map<String, Object> groupsMap = processData.getGroupsMap();
		List<ReplyName> replyNameList = getReplyNameList(processData);
		List<Map<String, Object>> groupsList = (List<Map<String, Object>>) MapUtils.getObject(groupsMap, Const.G_NAME_LIST, new ArrayList<Map<String, Object>>());
		List<Map<String, Object>> newGroupsList = new ArrayList<Map<String, Object>>();
		try {
			Map<String, Object> GroupMap;
			for (Map<String, Object> map : groupsList) {
				GroupMap = new HashMap<String, Object>();
				String groupName = MapUtils.getString(map, Const.NAME);
				GroupMap.put(Const.NAME, groupName);
				if (replyNameList != null) {
					for (ReplyName replyName : replyNameList) {
						if (Const.GROUP == replyName.getType() && groupName.equals(replyName.getMarkName())) {
							GroupMap.put(Const.SELECT, Const.Y);
						}
					}
				}
				newGroupsList.add(GroupMap);
			}
		} catch (Exception e) {
			retcode = Const.EXCEPTION_CODE;
			logger.error("群列表构建异常", e);
		}
		newGroupsMap.put(Const.RESULT, newGroupsList);
		newGroupsMap.put(Const.RET_CODE, retcode);
		processData.setGroupsViewMap(newGroupsMap);
		return newGroupsMap;
	}
	
	// 获取自动回复的名单（如果之前设置过）
	private List<ReplyName> getReplyNameList(ProcessData processData) {
		AutoReply autoReply = processData.getAutoReply();
		List<ReplyName> replyNameList = (autoReply == null ? null : autoReply.getReplyNameList());
		return replyNameList;
	}

	@Override
	@Transactional
	public void updateReplyNameList(Map<String, Object> paramMap, ProcessData processDataSession) throws RobotException {
		try {
			// 插入新数据，如果存在则更新为启用
			List<Map<String, Object>> addList = (ArrayList<Map<String, Object>>) MapUtils.getObject(paramMap, Const.ADD_KEY, new ArrayList<Map<String, Object>>());
			// 更新数据库，禁用回复名单
			List<Map<String, Object>> delList = (ArrayList<Map<String, Object>>) MapUtils.getObject(paramMap, Const.DEL_KEY, new ArrayList<Map<String, Object>>());
			// 入参校验
			updateReplyNameListParamsCheck(processDataSession, addList, delList);
			if (!addList.isEmpty()) {
				robotDao.insertOrUpdateReplyNameList(addList, processDataSession.getSelfUiu());
			}
			if (!delList.isEmpty()) {
				robotDao.disableReplyNameList(delList, processDataSession.getSelfUiu());
			}
			// 更新过程数据
			setAutoReply(processDataSession);
			// 页面联系人视图设置过期标识（需要更新）
			contactsListViewExpired(processDataSession);
		} catch (Exception e) {
			logger.error("设置自定义回复名单异常", e);
			// 所有编译期异常转化为运行期异常，便于事务回滚
			throw new RobotException(e);
		}
	}

	private void updateReplyNameListParamsCheck(ProcessData processDataSession, List<Map<String, Object>> addList,
			List<Map<String, Object>> delList) {
		if (addList.isEmpty() && delList.isEmpty()) {
			throwIllegalParamsException(processDataSession);
		}
		for (Map<String, Object> map : addList) {
			paramsMapCheck(processDataSession, map);
		}
		for (Map<String, Object> map : delList) {
			paramsMapCheck(processDataSession, map);
		}
	}

	private void paramsMapCheck(ProcessData processDataSession, Map<String, Object> map) {
		if (!map.containsKey(Const.NAME) || !map.containsKey(Const.TYPE)) {
			throwIllegalParamsException(processDataSession);
		} else {
			int type = MapUtils.getByteValue(map, Const.TYPE);
			if (!(type == Const.PERSON || type == Const.DISCU || type == Const.GROUP)) {
				throwIllegalParamsException(processDataSession);
			}
		}
	}

	private void throwIllegalParamsException(ProcessData processDataSession) {
		logger.error("提交非法入参，用户QQ号：{}", processDataSession.getSelfUiu());;
		throw new RobotException("非法入参");
	}

	private void contactsListViewExpired(ProcessData processDataSession) {
		if (MapUtils.isNotEmpty(processDataSession.getFriendsViewMap())) {
			processDataSession.getFriendsViewMap().put(Const.RET_CODE, Const.LIST_VIEW_EXPIRED);
		}
		if (MapUtils.isNotEmpty(processDataSession.getGroupsViewMap())) {
			processDataSession.getGroupsViewMap().put(Const.RET_CODE, Const.LIST_VIEW_EXPIRED);
		}
		if (MapUtils.isNotEmpty(processDataSession.getDiscussesViewMap())) {
			processDataSession.getDiscussesViewMap().put(Const.RET_CODE, Const.LIST_VIEW_EXPIRED);
		}
	}

	@Override
	public void updateIsAutoReply(Map<String, Object> paramMap, ProcessData processDataSession) throws RobotException {
		try {
			int autoReplyfalg = 1;
			if (Const.ON.equals(MapUtils.getString(paramMap, Const.AUTO_REPLY))) {
				autoReplyfalg = 1;
			} else if (Const.OFF.equals(MapUtils.getString(paramMap, Const.AUTO_REPLY))) {
				autoReplyfalg = 0;
			} else {
				throwIllegalParamsException(processDataSession);
			}
			robotDao.updateIsAutoReply(autoReplyfalg, processDataSession.getSelfUiu());
			processDataSession.getAutoReply().setIsAutoReply(autoReplyfalg == 1 ? true : false);
		} catch (Exception e) {
			logger.error("设置自动回复异常", e);
			throw new RobotException(e);
		}
	}

	@Override
	public void updateIsSpecial(Map<String, Object> paramMap, ProcessData processDataSession) throws RobotException{
		try {
			int specialfalg = 0;
			if (Const.ON.equals(MapUtils.getString(paramMap, Const.REPLY_ALL))) {
				specialfalg = 1;
			} else if (Const.OFF.equals(MapUtils.getString(paramMap, Const.REPLY_ALL))) {
				specialfalg = 0;
			} else {
				throwIllegalParamsException(processDataSession);
			}
			robotDao.updateIsSpecial(specialfalg, processDataSession.getSelfUiu());
			processDataSession.getAutoReply().setIsSpecial(specialfalg == 1 ? true : false);
		} catch (Exception e) {
			logger.error("设置自定义回复异常", e);
			throw new RobotException(e);
		}
	}

	@Override
	public void quit(ProcessData processDataSession) {
		// 1.结束线程轮询
		try {
			Future<?> future = CacheMap.threadFuturMap.get(processDataSession.getSelfUiu());
			if (null != future && !future.isDone()) {
				future.cancel(true);
			}
		} catch (Exception e) {
			logger.error("线程结束异常", e);
			throw new RobotException(e);
		}
		// 2.过程数据引用清除
		CacheMap.processDataMap.put(processDataSession.getSelfUiu(), new ProcessData());
		// 3.session引用清除
		this.session.removeAttribute(Const.PROCESS_DATA);
		// 4.记录退出更新时间
		try {
			robotDao.updateQuitTime(processDataSession.getUserInfo().getId());
		} catch (Exception e) {
			logger.error("更新退出时间异常", e);
		}
		// 5.清除引用
		processDataSession = null;
	}
	
	private void threadSleep() {
		try {
			Thread.sleep(Const.DELAY_TIME);
		} catch (InterruptedException e) {
			logger.error("线程睡眠异常", e);
		}
	}

	@Override
	public void sessionSetter(HttpSession session) {
		this.session = session;
	}
}
