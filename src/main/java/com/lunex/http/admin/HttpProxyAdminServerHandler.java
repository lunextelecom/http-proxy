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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.base.Strings;
import com.lunex.cassandra.CassandraRepository;
import com.lunex.enums.EAdminFunction;
import com.lunex.exceptions.BadRequestException;
import com.lunex.util.Configuration;
import com.lunex.util.Constants;

/**
 * Server handler for netty server
 */
public class HttpProxyAdminServerHandler extends SimpleChannelInboundHandler<HttpObject> {

  static final Logger logger = LoggerFactory.getLogger(HttpProxyAdminServerHandler.class);

  private HttpRequest request;
  
  private Exception exception;
  
  private EAdminFunction function;

  public HttpProxyAdminServerHandler() {}

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    // write response
    if (function == EAdminFunction.CHECKHEALTH) {
      exceptionCaught(ctx, exception);
      return;
    }
    HttpResponseStatus status = HttpResponseStatus.OK;
    String content = "Reload content OK";
    String contentType = "text/plain; charset=UTF-8";
    if(function == EAdminFunction.CHECKHEALTH){
      content = "OK";
    }else if(function == EAdminFunction.MONITOR){
      content = buildMonitorContent();
      contentType = "text/html; charset=UTF-8";
    }else{
      try {
        Configuration.reloadConfig();
      } catch (Exception e) {
        status = HttpResponseStatus.BAD_REQUEST;
        content = "Can not load config or config invalid";
      }
    }
    FullHttpResponse response =
        new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer(content,
            CharsetUtil.UTF_8));

    response.headers().set(CONTENT_TYPE, contentType);
    response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

    ctx.writeAndFlush(response);
    ctx.close();
  }
  private String buildMonitorContent(){
    ResultSet lstEndpoint = null;
    try {
      lstEndpoint = CassandraRepository.getInstance().getLstEndpointInfo();
    } catch (Exception e) {
      logger.error("getLstEndpointInfo error", e);
    }
    StringBuilder body = new StringBuilder();
    if (lstEndpoint != null) {
      String tmp = "<tr><td>%s</td><td style='text-align:center'><span style='background-color:%s'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></td><td style='text-align:center'>%s</td></tr>";
      while (!lstEndpoint.isExhausted()) {
        final Row row = lstEndpoint.one();
        body.append(String.format(tmp, row.getString("target"), row.getInt("status")==1?"green":"red", new SimpleDateFormat("dd/MM/yyyy").format(new Date(UUIDs.unixTimestamp(row.getUUID("updateid"))))));
      }
    }
    StringBuilder res = new StringBuilder("<html><head><title>jQuery Hello World</title><link rel=stylesheet type=text/css href=http://cdn.datatables.net/1.10.2/css/jquery.dataTables.css><script type=text/javascript charset=utf8 src=http://code.jquery.com/jquery-1.10.2.min.js></script><script type=text/javascript charset=utf8 src=http://cdn.datatables.net/1.10.2/js/jquery.dataTables.js></script><body><div style=width:500px><table id=table_id class=display><thead><tr><th>Target<th>Status<th>Update time<tbody>%s</table></div><script>$(document).ready(function(){$('#table_id').DataTable({autoWidth:!1,paging:!1,columns:[{orderable:!0},{orderable:!1},{orderable:!1}]})});</script>");
    
    return String.format(res.toString(), body.toString());
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
      Matcher matcher = Configuration.getCheckHealthPattern().matcher(this.request.getUri());
      if(matcher.find()){
        function = EAdminFunction.CHECKHEALTH;
        return;
      }
      matcher = Configuration.getMonitorPattern().matcher(this.request.getUri());
      if(matcher.find()){
        function = EAdminFunction.MONITOR;
        return;
      }
      if (!authenticateFilter()) {
        function = EAdminFunction.EXCEPTION;
        return;
      }
      matcher = Configuration.getReloadPattern().matcher(this.request.getUri());
      if(!matcher.find()){
        exception = new BadRequestException(new Exception("unknow command"));
        function = EAdminFunction.EXCEPTION;
        return;
      }
    }
  }
}
