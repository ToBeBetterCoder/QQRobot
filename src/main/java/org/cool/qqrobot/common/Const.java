package org.cool.qqrobot.common;
/**
 * 常量
 * @author zhoukl
 *
 */
public class Const {
	/**
	 * session Key
	 */
	public static final String PROCESS_DATA = "PROCESS_DATA";
	/**
	 * httpclient线程池最大数量
	 */
	public static final int MAX_TOTAL = 10;
	/**
	 * 每个路由并发请求最大数量
	 */
	public static final int MAX_PER_ROUTE = 10;
	/**
	 * UTF-8编码
	 */
	public static final String ENCODING_UTF_8 = "UTF-8";
	/**
	 * ptwebqq返回值
	 */
	public static final String PTWEBQQ = "ptwebqq";
	/**
	 * vfwebqq返回值
	 */
	public static final String VFWEBQQ = "vfwebqq";
	/**
	 * 固定线程池大小
	 */
	public static final int FIXED_THREAD_POOL_NUM = 30;
	/**
	 * 客户端是否授权登录，循环监测次数
	 */
	public static final int CYCLE_NUM = 120;
	/**
	 * 延迟时间
	 */
	public static final long DELAY_TIME = 1000;
	/**
	 * json返回结果标识
	 */
	public static final String RET_CODE = "retcode";
	/**
	 * 成功返回值
	 */
	public static final Integer SUCCESS_CODE = 0;
	/**
	 * 异常默认值
	 */
	public static final Integer EXCEPTION_CODE = -1;
	/**
	 * 联系人列表失效，需重新构建
	 */
	public static final Integer LIST_VIEW_EXPIRED = 1;
	/**
	 * 返回结果集的key
	 */
	public static final String RESULT = "result";
	public static final String ACCOUNT = "account";
	
	public static final String GENDER = "gender";
	public static final String NICK = "nick";
	public static final String LNICK = "lnick";
	public static final String MARK_NAMES = "marknames";
	public static final String MARK_NAME = "markname";
	public static final String NAME = "name";
	public static final String INFO = "info";
	public static final String FRIENDS = "friends";
	public static final String G_NAME_LIST = "gnamelist";
	public static final String G_ID = "gid";
	public static final String D_NAME_LIST = "dnamelist";
	public static final String D_ID = "did";
	public static final String POLL_TYPE = "poll_type";
	public static final String VALUE = "value";
	public static final String FROM_UIN = "from_uin";
	public static final String CONTENT = "content";
	public static final String AT = "@";
	public static final String R_NEW_LINE = "\n";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";
	public static final String CODE = "code";
	public static final String TEXT = "text";
	public static final String URL = "url";
	public static final String LIST = "list";
	public static final String SOURCE = "source";
	public static final String ARTICLE = "article";
	public static final String DETAIL_URL = "detailurl";
	/**
	 * header key
	 */
	public static final String REFERER = "Referer";
	public static final String REFERER_S = "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1";
	public static final String REFERER_D = "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2";
	public static final String REFERER_D_S = "https://d1.web2.qq.com/cfproxy.html?v=20151105001&callback=1";
	/**
	 * 入参key
	 */
	public static final String R = "r";
	/**
	 * 账号
	 */
	public static final String UIN = "uin";
	/**
	 * 个人标识
	 */
	public static final String P_SESSION_ID = "psessionid";
	public static final String JAVA_SCRIPT = "javascript";
	public static final String JS_FILE_NAME = "hash.js";
	public static final String JS_HASH = "hash2";
	/**
	 * war包下class目录名称
	 */
	public static final CharSequence CLASSES = "classes";
	/**
	 * WEB-INF下的js目录名称
	 */
	public static final CharSequence JS = "js";
	/**
	 * 消息轮询线程池数量
	 */
	public static final int SCHEDULED_THREAD_POOL_NUM = 50;
	/**
	 * 线程启动延迟时间
	 */
	public static final long INIT_DELAY = 0;
	/**
	 * 间隔时间
	 */
	public static final long PERIOD = 10;
	/**
	 * redis连接池数量（默认8）
	 */
	public static final int MAX_ACTIVE = 30;
	/**
	 * 个人
	 */
	public static final byte PERSON = 0;
	/**
	 * 讨论组
	 */
	public static final byte DISCU = 1;
	/**
	 * 群
	 */
	public static final byte GROUP = 2;
	public static final String MESSAGE = "message";
	public static final String DISCU_MESSAGE = "discu_message";
	public static final String GROUP_MESSAGE = "group_message";
	public static final String ERR_CODE = "errCode";
	public static final String API_KEY = "44bc1820944e4af68bbf97a7b57992cf";
	public static final String NEW_LINE = "\\n";
	/**
	 * 新闻显示条数（原样返回，条数太多，似乎不能接收到消息）
	 */
	public static final int NEW_MAX_NUM = 3;
	/**
	 * 菜谱显示条数（原样返回，条数太多，似乎不能接收到消息）
	 */
	public static final int COOK_MAX_NUM = 3;
	public static final String ROOT_PATH = "/";
	public static final String CATEGORIES = "categories";
	public static final String CATE_INDEX = "cateIndex";
	public static final String CATE_NAME = "cateName";
	public static final String CATE_NAME_VAL = "我的好友";
	public static final String CATE_SORT = "cateSort";
	public static final String INDEX = "index";
	public static final String SORT = "sort";
	public static final String CATE = "cate";
	public static final String SELECT = "select";
	public static final String Y = "Y";
	public static final String ADD_KEY = "add";
	public static final String DEL_KEY = "del";
	public static final String AUTO_REPLY = "autoReply";
	public static final String REPLY_ALL = "replyAll";
	public static final String ON = "on";
	public static final String OFF = "off";
	public static final Object TYPE = "type";
	public static final String RES_VERSION = "resVer";
	public static final String DATE_Y_M_D_H_M_S = "yyyyMMddHHmmss";
	/**
	 * 二维码失效
	 */
	public static final Integer INVALID_CODE = 65;
	public static final String PATH_PREFIX = "file:";
	public static final String OS_NAME = "os.name";
	public static final String WINDOWS = "Windows";
	public static final String USER_AGENT = "User-Agent";
	public static final String COUNT = "count";
	
}
