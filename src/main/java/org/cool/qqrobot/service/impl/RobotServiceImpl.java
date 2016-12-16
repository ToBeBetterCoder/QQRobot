package org.cool.qqrobot.service.impl;

import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.cool.qqrobot.common.CacheMap;
import org.cool.qqrobot.common.Const;
import org.cool.qqrobot.common.RobotCodeEnums;
import org.cool.qqrobot.dao.RobotDao;
import org.cool.qqrobot.dao.cache.RedisDao;
import org.cool.qqrobot.entity.MyHttpRequest;
import org.cool.qqrobot.entity.MyHttpResponse;
import org.cool.qqrobot.entity.ProcessData;
import org.cool.qqrobot.entity.ReplyName;
import org.cool.qqrobot.entity.UserInfo;
import org.cool.qqrobot.service.RobotService;
import org.cool.qqrobot.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RobotServiceImpl implements RobotService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RobotDao robotDao;
	@Autowired
	private RedisDao redisDao;
	@Override
	public MyHttpResponse getCode(ProcessData processData) {
		MyHttpRequest codeRequest = new MyHttpRequest();
		codeRequest.setUrl("https://ssl.ptlogin2.qq.com/ptqrshow?appid=501004106&e=0&l=M&s=5&d=72&v=4&t=" + Math.random());
		MyHttpResponse codeResponse = new MyHttpResponse();
		try {
			codeResponse = processData.getMyHttpClient().execute(codeRequest);
		} catch (Exception e) {
			logger.error("二维码获取异常", e);
		}
		if (MyHttpResponse.S_OK == codeResponse.getStatus()) {
			loginCheck(processData);
		} else {
			processData.setLogin(false);
		}
		return codeResponse;
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
						processData.setLogin(false);
						logger.error("二维码登录状态获取异常", e);
					}
					boolean firstLoginSuccess = false;
					if (MyHttpResponse.S_OK == checkResponse.getStatus()) {
						firstLoginSuccess = firstLogin(processData, checkResponse);
					} else {
						processData.setLogin(false);
					}
					try {
						Thread.sleep(Const.DELAY_TIME);
					} catch (InterruptedException e) {
						logger.error("线程睡眠异常", e);
					}
					if (firstLoginSuccess) {
						break;
					}
					// 轮询时间结束，用户还未扫描登录，自动标识为登录失败（不然下次不能获取二维码，刷新页面二维码不变）
					if (i == Const.CYCLE_NUM - 1) {
						processData.setLogin(false);
					}
				}
			}
		});
	}
	
	private boolean firstLogin(ProcessData processData, MyHttpResponse checkResponse) {
		String[] checkResponseArr = checkResponse.getTextStr().split(",");
		if (checkResponseArr[0].contains(Const.SUCCESS_CODE.toString())) {
			MyHttpRequest firstLoginRequest = new MyHttpRequest();
			firstLoginRequest.setUrl(checkResponseArr[2].replaceAll("'", ""));
			MyHttpResponse firstLoginResponse = new MyHttpResponse();
			try {
				firstLoginResponse = processData.getMyHttpClient().execute(firstLoginRequest);
			} catch (Exception e) {
				processData.setLogin(false);
				logger.error("first登陆异常", e);
			}
			if (MyHttpResponse.S_OK == firstLoginResponse.getStatus()) {
				processData.setPtwebqq(firstLoginResponse.getCookiesValue(Const.PTWEBQQ));
				getVfwebqq(processData);
				return true;
			} else {
				processData.setLogin(false);
			}
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
			processData.setLogin(false);
			logger.error("vfwebqq获取异常", e);
		}
		if (MyHttpResponse.S_OK == vfwebqqResponse.getStatus()) {
			if (Const.SUCCESS_CODE.equals(MapUtils.getInteger(vfwebqqResponse.getJsonMap(), Const.RET_CODE))) {
				processData.setVfwebqq(MapUtils.getString(MapUtils.getMap(vfwebqqResponse.getJsonMap(), Const.RESULT), Const.VFWEBQQ));
				secondLogin(processData);
			} else {
				processData.setLogin(false);
			}
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
			processData.setLogin(false);
			logger.error("second登录异常", e);
		}
		if (MyHttpResponse.S_OK == secondLoginResponse.getStatus()) {
			if (Const.SUCCESS_CODE.equals(MapUtils.getInteger(secondLoginResponse.getJsonMap(), Const.RET_CODE))) {
				processData.setSelfUiu(MapUtils.getDouble(MapUtils.getMap(secondLoginResponse.getJsonMap(), Const.RESULT), Const.UIN));
				processData.setPsessionid(MapUtils.getString(MapUtils.getMap(secondLoginResponse.getJsonMap(), Const.RESULT), Const.P_SESSION_ID));
				ProcessData dataFromCache = CacheMap.processDataMap.get(processData.getSelfUiu());
				// 防止重复登陆
				if (null == dataFromCache || !dataFromCache.isLogin()) {
					// 获取好友列表、群列表、讨论组列表、个人信息（异步）
					multipleInfoGet(processData);
					// 获取在线好友  否则如果不先登录webbQQ会报：{"errmsg":"error!!!","retcode":103} 无法获取消息和发送消息
					if (onlineBuddies(processData)) {
						// 设置允许自动回复的uin
						processData.setAutoReply(robotDao.queryAutoReplyNames(processData.getSelfUiu()));
						// 异步轮询获取消息 webQQ轮询获取消息机制：客户端发起一次poll请求,服务端进行轮询，1分钟没有消息返回，则返回{"errmsg":"error!!!","retcode":0}
						pollMessageThread(processData);
						// 设置最终登录成功标志
						processData.setLogin(true);
						// 更新缓存信息
						CacheMap.processDataMap.put(processData.getSelfUiu(), processData);
					}
					
				}
			} else {
				processData.setLogin(false);
			}
		}
	}

	private void multipleInfoGet(ProcessData processData) {
		friendsList(processData);
		groupsList(processData);
		discussesList(processData);
		ThreadPool.getInstance().getFixedThreadPool().execute(new Runnable() {
			
			@Override
			public void run() {
				selfInfo(processData);
			}
		});
	}

	private void pollMessageThread(ProcessData processData) {
		// TODO 把每一个轮询线程对象保存到map中，便于终止轮询（key:qq号，value:Future对象），同时清除登录成功的缓存信息
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
					processData.setLogin(false);
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
	}
	
	private boolean autoReply(ProcessData processData, String fromUin) {
		boolean isReply = false;
		if (null == processData.getAutoReply()) {
			return isReply;
		}
		if (processData.getAutoReply().isSpecial()) {
			List<ReplyName> replyNameList = processData.getAutoReply().getReplyNameList();
			for (ReplyName replyName : replyNameList) {
				if (fromUin.equals(replyName.getUin())) {
					isReply = true;
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
	
	private void friendsList(ProcessData processData) {
		MyHttpRequest friendsRequest = new MyHttpRequest(HttpPost.METHOD_NAME);
		friendsRequest.getHeaderMap().put(Const.REFERER, Const.REFERER_S);
		friendsRequest.setUrl("http://s.web2.qq.com/api/get_user_friends2");
		friendsRequest.getPostMap().put(Const.R, "{\"vfwebqq\":\"" + processData.getVfwebqq() + "\",\"hash\":\"" + hashByJs(processData.getSelfUiu(), processData.getPtwebqq()) + "\"}");
		MyHttpResponse friendsResponse = new MyHttpResponse();
		try {
			friendsResponse = processData.getMyHttpClient().execute(friendsRequest);
		} catch (Exception e) {
			processData.setLogin(false);
			logger.error("获取好友列表异常", e);
		}
		if (MyHttpResponse.S_OK == friendsResponse.getStatus()) {
			if (Const.SUCCESS_CODE.equals(MapUtils.getInteger(friendsResponse.getJsonMap(), Const.RET_CODE))) {
				processData.setFriendsMap(MapUtils.getMap(friendsResponse.getJsonMap(), Const.RESULT));
			} else {
				logger.error("获取好友列表异常");
			}
		}
	}

	private void groupsList(ProcessData processData) {
		MyHttpRequest groupsRequest = new MyHttpRequest(HttpPost.METHOD_NAME);
		groupsRequest.getHeaderMap().put(Const.REFERER, Const.REFERER_S);
		groupsRequest.setUrl("http://s.web2.qq.com/api/get_group_name_list_mask2");
		groupsRequest.getPostMap().put(Const.R, "{\"vfwebqq\":\"" + processData.getVfwebqq() + "\",\"hash\":\"" + hashByJs(processData.getSelfUiu(), processData.getPtwebqq()) + "\"}");
		MyHttpResponse groupsResponse = new MyHttpResponse();
		try {
			groupsResponse = processData.getMyHttpClient().execute(groupsRequest);
		} catch (Exception e) {
			processData.setLogin(false);
			logger.error("获取群列表异常", e);
		}
		if (MyHttpResponse.S_OK == groupsResponse.getStatus()) {
			if (Const.SUCCESS_CODE.equals(MapUtils.getInteger(groupsResponse.getJsonMap(), Const.RET_CODE))) {
				processData.setGroupsMap(MapUtils.getMap(groupsResponse.getJsonMap(), Const.RESULT));
			} else {
				logger.error("获取群列表异常");
			}
		}
	}

	private void discussesList(ProcessData processData) {
		MyHttpRequest discussesRequest = new MyHttpRequest();
		discussesRequest.getHeaderMap().put(Const.REFERER, Const.REFERER_S);
		discussesRequest.setUrl("http://s.web2.qq.com/api/get_discus_list?clientid=53999199&psessionid=" + processData.getPsessionid() + "&vfwebqq=" + processData.getVfwebqq());
		MyHttpResponse discussesResponse = new MyHttpResponse();
		try {
			discussesResponse = processData.getMyHttpClient().execute(discussesRequest);
		} catch (Exception e) {
			processData.setLogin(false);
			logger.error("获取讨论组列表异常", e);
		}
		if (MyHttpResponse.S_OK == discussesResponse.getStatus()) {
			if (Const.SUCCESS_CODE.equals(MapUtils.getInteger(discussesResponse.getJsonMap(), Const.RET_CODE))) {
				processData.setDiscussesMap(MapUtils.getMap(discussesResponse.getJsonMap(), Const.RESULT));
			} else {
				logger.error("获取讨论组列表异常");
			}
		}
	}

	private void selfInfo(ProcessData processData) {
		MyHttpRequest selfInfoRequest = new MyHttpRequest();
		selfInfoRequest.getHeaderMap().put(Const.REFERER, Const.REFERER_S);
		selfInfoRequest.setUrl("http://s.web2.qq.com/api/get_self_info2");
		MyHttpResponse selfInfoResponse = new MyHttpResponse();
		try {
			selfInfoResponse = processData.getMyHttpClient().execute(selfInfoRequest);
		} catch (Exception e) {
			processData.setLogin(false);
			logger.error("获取个人信息异常", e);
		}
		if (MyHttpResponse.S_OK == selfInfoResponse.getStatus()) {
			if (Const.SUCCESS_CODE.equals(MapUtils.getInteger(selfInfoResponse.getJsonMap(), Const.RET_CODE))) {
				UserInfo userInfo = new UserInfo(selfInfoResponse.getJsonMap(), selfInfoResponse.getTextStr());
				processData.setUserInfo(userInfo);
				// 异步记录登录用户信息
				// TODO 登录成功后，记录当前登录日志的id，当用户主动退出时，再次更新登出时间
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
			processData.setLogin(false);
			logger.error("获取在线好友列表异常", e);
		}
		if (MyHttpResponse.S_OK == onlineBuddiesResponse.getStatus()) {
			if (Const.SUCCESS_CODE.equals(MapUtils.getInteger(onlineBuddiesResponse.getJsonMap(), Const.RET_CODE))) {
				processData.setOnlineBuddiesMap(MapUtils.getMap(onlineBuddiesResponse.getJsonMap(), Const.RESULT));
				return true;
			} else {
				processData.setLogin(false);
			}
		}
		return false;
	}

	private String hashByJs(String selfUiu, String ptwebqq) {
		ScriptEngineManager manager = new ScriptEngineManager(); 
		ScriptEngine engine = manager.getEngineByName(Const.JAVA_SCRIPT);     
		// 读取js文件 
		String jsFileName = this.getClass().getClassLoader().getResource("/").getPath().replace(Const.CLASSES, Const.JS).substring(1) + Const.JS_FILE_NAME; 
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
}
