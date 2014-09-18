package com.lunex.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;

/**
 * Callback for response get from client handler
 * 
 * @author BaoLe
 *
 */

public class CallbackHTTPVisitor {
	
	//public Object result;

	public void doJob(ChannelHandlerContext ctx, HttpObject msg) {
		return;
	}
}
