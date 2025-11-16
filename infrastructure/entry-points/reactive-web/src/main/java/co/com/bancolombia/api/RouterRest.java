package co.com.bancolombia.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(
            FranchiseHandler franchiseHandler,
            BranchHandler branchHandler,
            ProductHandler productHandler) {
        return route(POST("/api/v1/franchise"), franchiseHandler::saveFranchise)
                .andRoute(POST("/api/v1/branch"), branchHandler::saveBranch)
                .andRoute(POST("/api/v1/product"), productHandler::saveProduct)
                .andRoute(DELETE("/api/v1/branch/{branchId}/product/{productId}"), productHandler::deleteProduct)
                .andRoute(PATCH("/api/v1/product/{productId}/stock"), productHandler::updateStock)
                .andRoute(GET("/api/v1/franchise/{franchiseId}/top-stock-products"), productHandler::getTopProductsByStock);
    }
}
