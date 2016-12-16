package org.cool.qqrobot.dao.cache;

import org.cool.qqrobot.common.Const;
import org.cool.qqrobot.entity.ProcessData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis缓存操作
 * @author zhoukl
 *
 */
public class RedisDao {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final JedisPool jedisPool;

	public RedisDao(String ip, int port) {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxActive(Const.MAX_ACTIVE);
		this.jedisPool = new JedisPool(jedisPoolConfig, ip, port);
	}
	
	private RuntimeSchema<ProcessData> schema = RuntimeSchema.createFrom(ProcessData.class);
	
	public ProcessData getProcessData(String uiu) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String key = uiu;
			// 采用自定义序列化
			byte[] bytes = jedis.get(key.getBytes());
			// 缓存获取
			if (null != bytes) {
				// 空对象
				ProcessData processData = schema.newMessage();
				ProtobufIOUtil.mergeFrom(bytes, processData, schema);
				// processData 反列化
				return processData;
			}
		} catch (Exception e) {
			jedisPool.returnBrokenResource(jedis);
			logger.error(e.getMessage(), e);
		} finally {
			if (null != jedis) {
				jedisPool.returnResource(jedis);
			}
		}
		return null;
	}
	
	public String putProcessData(ProcessData processData) {
		// set Object(processData) ->序列化->byte[]
		Jedis jedis = null;
		String result = "";
		try {
			jedis = jedisPool.getResource();
			String key = processData.getSelfUiu();
			byte[] bytes = ProtostuffIOUtil.toByteArray(processData, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
			result = jedis.set(key.getBytes(), bytes);
		} catch (Exception e) {
			jedisPool.returnBrokenResource(jedis);
			logger.error(e.getMessage(), e);
		} finally {
			if (null != jedis) {
				jedisPool.returnResource(jedis);
			}
		}
		return result;
	}
	
}
