package com.example.kppgateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.SetRequestHostHeaderGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.net.URI;

@Component
public class MaintainPortFilter extends AbstractGatewayFilterFactory<MaintainPortFilter.Config> {

    protected String filterUserRole = null;
    SetRequestHostHeaderGatewayFilterFactory a;

    public MaintainPortFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> {
            try {
                URI responseUri =  exchange.getResponse().getHeaders().getLocation();
                if (responseUri == null) {
                    return;
                }
                URI requestUri = exchange.getRequest().getURI();
                int port = exchange.getRequest().getHeaders().getHost().getPort();
                exchange.getResponse().getHeaders().setLocation(
                        new URI(responseUri.getScheme(), responseUri.getUserInfo(), requestUri.getHost(), port, responseUri.getPath(), responseUri.getQuery(), responseUri.getFragment()));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }));

    }

    public static class Config {
    }
}

