package com.github.dapeng.gateway.netty.handler;

import com.github.dapeng.gateway.http.HttpProcessorUtils;
import com.github.dapeng.gateway.http.match.UrlMappingResolverNew;
import com.github.dapeng.gateway.netty.request.RequestContext;
import com.github.dapeng.gateway.util.DapengMeshCode;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author maple 2018.08.23 上午10:01
 */
@ChannelHandler.Sharable
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static Logger logger = LoggerFactory.getLogger(HttpRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        RequestContext context = new RequestContext();
        try {
            HttpMethod httpMethod = request.method();
            String url = request.uri();

            context.httpMethod(httpMethod);
            context.requestUrl(url);
            // POST FIRST
            if (HttpMethod.POST.equals(httpMethod)) {
                UrlMappingResolverNew.handlerPostUrl(request, context);
            }


            boolean isGet = HttpMethod.GET.equals(httpMethod);
            if (isGet || HttpMethod.HEAD.equals(httpMethod)) {
                logger.info("For the time being, no message to log for Get-method");
            }

        } catch (Exception e) {
            logger.error("网关处理请求失败: " + e.getMessage(), e);
            HttpProcessorUtils.sendHttpResponse(ctx, HttpProcessorUtils.wrapErrorResponse(DapengMeshCode.ProcessReqFailed), null, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
