package com.example.service;

import java.util.HashMap;

import io.netty.channel.Channel;

public class NettyDemoTcpChannelRepository {
	private HashMap<String, Channel> channelCache = new HashMap<String, Channel>();

	public Channel get(String key) {
		return channelCache.get(key);
	}

	public NettyDemoTcpChannelRepository put(String key, Channel value) {
		channelCache.put(key, value);
		return this;
	}
	
	public void remove(String key) {
		this.channelCache.remove(key);
	}
	
	public int size() {
		return this.channelCache.size();
	}
}
