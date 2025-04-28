package com.nttdata.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class RequestTimingFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        long start = System.currentTimeMillis();
        return Mono.fromRunnable(() -> {
            exchange.getResponse().beforeCommit(() -> {
                long duration = System.currentTimeMillis() - start;
                exchange.getResponse().getHeaders().add("X-Duration", duration + " ms");
                return Mono.empty();
            });
        }).then(chain.filter(exchange));

    }
}
