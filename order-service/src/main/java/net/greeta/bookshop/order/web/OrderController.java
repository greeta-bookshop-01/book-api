package net.greeta.bookshop.order.web;

import lombok.RequiredArgsConstructor;
import net.greeta.bookshop.helper.JwtHelper;
import net.greeta.bookshop.order.domain.OrderService;
import net.greeta.bookshop.order.domain.Order;
import jakarta.validation.Valid;
import net.greeta.bookshop.security.JwtAuthConverterProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
public class OrderController {

	private static final Logger log = LoggerFactory.getLogger(OrderController.class);
	private final OrderService orderService;

	private final JwtAuthConverterProperties jwtAuthConverterProperties;

	@GetMapping
	public Flux<Order> getAllOrders(@AuthenticationPrincipal Jwt jwt) {
		log.info("Fetching all orders");
		return jwt == null ? Flux.empty() : orderService.getAllOrders(JwtHelper.getUsername(jwt, jwtAuthConverterProperties));
	}

	@PostMapping
	public Mono<Order> submitOrder(@RequestBody @Valid OrderRequest orderRequest) {
		log.info("Order for {} copies of the book with ISBN {}", orderRequest.quantity(), orderRequest.isbn());
		return orderService.submitOrder(orderRequest.isbn(), orderRequest.quantity());
	}

}
