package com.powsybl.afs.ws.server.sb;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.powsybl.afs.ws.server.utils.sb.AppDataBeanSB;
import com.powsybl.afs.ws.utils.AfsRestApi;

@Configuration
@EnableWebSocket
public class WebSocketServerSB   implements WebSocketConfigurer  {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketServerSB.class);

    @Autowired(required = true)
    private AppDataBeanSB appDataBean;

    @Autowired(required = true)
    private WebSocketContextSB webSocketContext;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
            .addHandler(new NodeEventHandlerSB(appDataBean, webSocketContext),  "/messages/" + AfsRestApi.RESOURCE_ROOT + "/" + AfsRestApi.VERSION + "/node_events/{fileSystemName}")
            .addHandler(new TaskEventHandlerSB(appDataBean, webSocketContext), "/messages/" + AfsRestApi.RESOURCE_ROOT + "/" + AfsRestApi.VERSION + "/task_events/{fileSystemName}/{projectId}")
            .setAllowedOrigins("*")
            .addInterceptors(new UriTemplateHandshakeInterceptor());
    }

    private class UriTemplateHandshakeInterceptor implements HandshakeInterceptor {
        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
            HttpServletRequest origRequest = ((ServletServerHttpRequest) request).getServletRequest();
            /* Retrieve template variables */
            Map<String, String> uriTemplateVars = (Map<String, String>) origRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            /* Put template variables into WebSocket session attributes */
            if (uriTemplateVars != null) {
                attributes.putAll(uriTemplateVars);
            }
            return true;
        }
        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        }
    }
}