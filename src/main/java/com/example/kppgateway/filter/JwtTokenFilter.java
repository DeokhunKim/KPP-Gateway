package com.example.kppgateway.filter;

import com.example.kppgateway.config.TokenKeyConst;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class JwtTokenFilter extends AbstractGatewayFilterFactory<JwtTokenFilter.Config> {

    private String secretKey = "TheKoon";

    public JwtTokenFilter() {
        super(Config.class);
    }

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Request Header 에 token 이 존재하지 않을 때
            if(!request.getCookies().containsKey(TokenKeyConst.TOKEN_KEY)){
                return handleHttpStatus(exchange, HttpStatus.UNAUTHORIZED); // 401 Error
            }

            // Request Header 에서 token 문자열 받아오기
            List<HttpCookie> token = request.getCookies().get(TokenKeyConst.TOKEN_KEY);
            String tokenString = Objects.requireNonNull(token).get(0).getValue();

            // 토큰 검증
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(secretKey)
                        .parseClaimsJws(tokenString)
                        .getBody();
                String user = claims.getSubject();
                Object roles = claims.get("roles");
                ServerHttpRequest newRequest = exchange.getRequest().mutate()
                        .headers(it -> it.add("userName", user))
                        .headers(it -> it.add("userRoles", roles.toString()))
                        .build();

                return chain.filter(exchange.mutate().request(newRequest).build()); // 토큰이 일치할 때
            }
            // 토큰 만료
            catch (ExpiredJwtException e) {
                log.info("Permission denied token expired.");
                return handleHttpStatus(exchange, HttpStatus.UNAUTHORIZED);
            }
            // Signature 오류
            catch (SignatureException e) {
                log.info("Permission denied wrong signature.");
                return handleHttpStatus(exchange, HttpStatus.UNAUTHORIZED);
            }
            // 문자열 오류
            catch (IllegalArgumentException e) {
                log.info("Permission denied IllegalArgumentException.");
                return handleHttpStatus(exchange, HttpStatus.UNAUTHORIZED);
            }

        };

    }

    private Mono<Void> handleHttpStatus(ServerWebExchange exchange, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(status);
        return response.setComplete();
    }

    public static class Config {
    }
}

