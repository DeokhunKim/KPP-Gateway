package com.example.kppgateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.SetRequestHostHeaderGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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
                URI uri =  exchange.getResponse().getHeaders().getLocation();
                if (uri == null) {
                    return;
                }
                int port = exchange.getRequest().getHeaders().getHost().getPort();
                exchange.getResponse().getHeaders().setLocation(
                        new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), port, uri.getPath(), uri.getQuery(), uri.getFragment()));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }));

    }

    public static class Config {
    }
}

