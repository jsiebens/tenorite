package net.tenorite.web.socket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

class ReplayHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler webSocketHandler, Map<String, Object> attributes) throws Exception {
        MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromHttpRequest(request).build().getQueryParams();
        String tempo = queryParams.getFirst("tempo");
        String id = queryParams.getFirst("id");

        attributes.put(Parameters.TEMPO, tempo);
        attributes.put(Parameters.ID, id);

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }

}
