package org.example.apigateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouterFunction<ServerResponse> roomServiceRoute() {
        return route("room-service-route")
                .route(RequestPredicates.path("/api/rooms/**"), http())
                .filter(lb("room-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> memberServiceRoute() {
        return route("member-service-route")
                .route(RequestPredicates.path("/api/members/**"), http())
                .filter(lb("member-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> reservationServiceRoute() {
        return route("reservation-service-route")
                .route(RequestPredicates.path("/api/reservations/**"), http())
                .filter(lb("reservation-service"))
                .build();
    }
}