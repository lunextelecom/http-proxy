package com.lunex.http.admin;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.lunex.exceptions.BadRequestException;
import com.lunex.util.Configuration;
import com.lunex.util.Constants;

/**
 * Server handler for netty server
 */
public class HttpProxyAdminServerHandler extends SimpleChannelInboundHandler<HttpObject> {

  static final Logger logger = LoggerFactory.getLogger(HttpProxyAdminServerHandler.class);

  private HttpRequest request;
  
  private Boolean isException = false;
  
  private Exception exception;

  public HttpProxyAdminServerHandler() {}

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    // write response
    if (isException) {
      exceptionCaught(ctx, exception);
      return;
    }
    HttpResponseStatus status = HttpResponseStatus.OK;
    String content = "Reload content OK";
    try {
      Configuration.reloadConfig();
    } catch (Exception e) {
      status = HttpResponseStatus.BAD_REQUEST;
      content = "Can not load config or config invalid";
    }
    FullHttpResponse response =
        new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer(content,
            CharsetUtil.UTF_8));

    response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
    response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

    ctx.writeAndFlush(response);
    ctx.close();
  }

  /**
   * Authenticate filter.
   *
   * @return true, if authenticate success
   */
  private boolean authenticateFilter(){
    boolean res = true;
    String username = this.request.headers().get(Constants.USERNAME_PRO);
    String password = this.request.headers().get(Constants.PASSWORD_PRO);
    if (Strings.isNullOrEmpty(username) || Strings.isNullOrEmpty(password)) {
      logger.info("authenticate failed");
      exception = new BadRequestException(new Exception("authenticate failed"));
      res = false;
    } else {
      if (!Constants.AUTH_STR.equalsIgnoreCase(username)
          || !Constants.AUTH_STR.equalsIgnoreCase(password)) {
        logger.info("authenticate failed");
        exception = new BadRequestException(new Exception("authenticate failed"));
        res = false;
      }
    }
    return res;
  }
  


  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.error("Exception caught", cause);

    HttpResponseStatus status =  (cause instanceof BadRequestException) ? BAD_REQUEST : INTERNAL_SERVER_ERROR;
    String content = cause.getMessage();
    FullHttpResponse response =
        new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer(content,
            CharsetUtil.UTF_8));

    response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
    response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

    ctx.writeAndFlush(response);
    ctx.close();
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
    if (msg instanceof HttpRequest) {
      this.request = (HttpRequest) msg;
      if (!authenticateFilter()) {
        isException = true;
        return;
      }
      Matcher matcher = Configuration.getReloadPattern().matcher(this.request.getUri());
      if(!matcher.find()){
        exception = new BadRequestException(new Exception("unknow command"));
        isException = true;
        return;
      }
    }
  }
}
