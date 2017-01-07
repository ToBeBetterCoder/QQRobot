package org.cool.qqrobot.dao;

import java.util.Map;
import java.util.Set;

import org.cool.qqrobot.entity.AutoReply;
import org.cool.qqrobot.entity.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit, spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RobotDaoTest {
	@Autowired
	RobotDao robotDao;
	
	@Test
	public void insertUserInfoTest() {
		Gson gson = new Gson();
		String json = "{\"retcode\":0,\"result\":{\"birthday\":{\"month\":6,\"year\":2015,\"day\":8},\"face\":594,\"phone\":\"\",\"occupation\":\"\",\"allow\":1,\"college\":\"\",\"uin\":3198803867,\"blood\":0,\"constel\":0,\"lnick\":\"\",\"vfwebqq\":\"5669b3d0fab3ab5751fb8e0228678b1074cb3899fee31f7133bf71c934f6034f4e8576964ab4ffba\",\"homepage\":\"\",\"vip_info\":0,\"city\":\"\",\"country\":\"冰岛\",\"personal\":\"不会解决一切重启电脑不能解决的问题\",\"shengxiao\":0,\"nick\":\"周凯林\",\"email\":\"\",\"province\":\"\",\"account\":3198803867,\"gender\":\"male\",\"mobile\":\"\"}}";
		UserInfo userInfo = new UserInfo(gson.fromJson(json, Map.class), json);
		robotDao.addLoginInfo(userInfo);
		System.out.println(userInfo.getId());
	}
	
	@Test
	public void queryAutoReplyNamesTest() throws Exception {
		AutoReply autoReplyNamesSet = robotDao.queryAutoReplyNames("815181993");
		System.out.println(autoReplyNamesSet);
	}
	
}
