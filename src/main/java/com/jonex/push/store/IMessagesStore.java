package com.jonex.push.store;

import com.jonex.push.event.PubRelEvent;
import com.jonex.push.event.PublishEvent;
import com.jonex.push.protocol.mqtt.message.Qos;
import io.netty.buffer.ByteBuf;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 *  消息存储接口
 *
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/27 14:32
 */
public interface IMessagesStore {

	public static class StoredMessage implements Serializable {
		final Qos qos;
        final byte[] payload;
        final String topic;

        public StoredMessage(byte[] message, Qos qos, String topic) {
            this.qos = qos;
            this.payload = message;
            this.topic = topic;
        }

        public Qos getQos() {
            return qos;
        }

        public byte[] getPayload() {
			return payload;
		}

		public String getTopic() {
            return topic;
        }
		
    }
	
	/**
	 * 初始化存储

	 */
	void initStore();
	
	/**
	 * 返回某个clientID的离线消息列表
	 * @param clientID
	 */
	List<PublishEvent> listMessagesInSession(String clientID);
	

	/**
	 * 在重发以后，移除publish的离线消息事件
	 * @param clientID
	 * @param packgeID
	 */
	void removeMessageInSessionForPublish(String clientID, Integer packgeID);
	
	/**
	 * 存储publish的离线消息事件，为CleanSession=0的情况做重发准备
	 * @param pubEvent
	 */
	void storeMessageToSessionForPublish(PublishEvent pubEvent);

	/**
	 * 存储Publish的包ID
	 * @param clientID
	 * @param packgeID
	 */
	void storePublicPackgeID(String clientID, Integer packgeID);
	
	/**
	 * 移除Publish的包ID
	 * @param clientID
	 */
	void removePublicPackgeID(String clientID);
	
	/**
	 * 移除PubRec的包ID
	 * @param clientID
	 * @author zer0
	 * @version 1.0
	 * @date 2015-05-21
	 */
	void removePubRecPackgeID(String clientID);
	
	/**
	 * 存储PubRec的包ID
	 * @param clientID
	 * @param packgeID
	 */
	void storePubRecPackgeID(String clientID, Integer packgeID);
	
	/**
	 * 当Qos>0的时候，临时存储Publish消息，用于重发
	 * @param publishKey
	 * @param pubEvent
	 */
	void storeQosPublishMessage(String publishKey, PublishEvent pubEvent);
	
	/**
	 * 在收到对应的响应包后，删除Publish消息的临时存储
	 * @param publishKey
	 */
	void removeQosPublishMessage(String publishKey);

	/**
	 * 获取临时存储的Publish消息，在等待时间过后未收到对应的响应包，则重发该Publish消息
	 * @param publishKey
	 * @return PublishEvent
	 */
	PublishEvent searchQosPublishMessage(String publishKey);
	
	/**
	 * 当Qos=2的时候，临时存储PubRel消息，在未收到PubComp包时用于重发
	 * @param pubRelKey
	 * @param pubRelEvent
	 */
	void storePubRelMessage(String pubRelKey, PubRelEvent pubRelEvent);
	
	/**
	 * 在收到对应的响应包后，删除PubRel消息的临时存储
	 * @param pubRelKey
	 */
	void removePubRelMessage(String pubRelKey);

	/**
	 * 获取临时存储的PubRel消息，在等待时间过后未收到对应的响应包，则重发该PubRel消息
	 * @param pubRelKey
	 */
	PubRelEvent searchPubRelMessage(String pubRelKey);
	
	/**
	 * 持久化存储保留Retain为1的指定topic的最新信息，该信息会在新客户端订阅某主题的时候发送给此客户端
	 * @param topic
	 * @param message
	 * @param qos
	 */
    void storeRetained(String topic, ByteBuf message, Qos qos);
    
    /**
	 * 删除指定topic的Retain信息
	 * @param topic
	 */
    void cleanRetained(String topic);
    
    /**
	 * 从Retain中搜索对应topic中保存的信息
	 * @param topic
	 */
    Collection<StoredMessage> searchRetained(String topic);
}
