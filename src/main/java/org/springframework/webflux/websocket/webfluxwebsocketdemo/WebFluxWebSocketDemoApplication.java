package org.springframework.webflux.websocket.webfluxwebsocketdemo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
public class WebFluxWebSocketDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebFluxWebSocketDemoApplication.class, args);
	}


	@GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> getStreaming() throws URISyntaxException {
		WebSocketClient client = new ReactorNettyWebSocketClient();

		EmitterProcessor<String> output = EmitterProcessor.create();

		Mono<Void> sessionMono =
				client.execute(new URI("ws://localhost:8080/echo"),
						session -> session.receive()
								.map(WebSocketMessage::getPayloadAsText)
								.subscribeWith(output)
								.then());

		return output.doOnSubscribe(s -> sessionMono.subscribe());
	}

	@Bean
	public HandlerMapping webSocketMapping() {
		Map<String, WebSocketHandler> map = new HashMap<>();

		map.put("/echo", session ->
				session.send(
						Flux.just("foo", "bar", "baz", "cux")
								.map(session::textMessage)));

		SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
		mapping.setUrlMap(map);
		mapping.setOrder(1);
		return mapping;
	}

	@Bean
	public WebSocketHandlerAdapter handlerAdapter() {
		return new WebSocketHandlerAdapter();
	}

}
