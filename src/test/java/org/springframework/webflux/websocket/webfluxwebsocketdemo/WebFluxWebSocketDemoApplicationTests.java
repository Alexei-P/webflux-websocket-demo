package org.springframework.webflux.websocket.webfluxwebsocketdemo;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebFluxWebSocketDemoApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	public void testWebSocketProxy() {
		Flux<String> responseBody =
				this.webTestClient.get().uri("/stream")
						.exchange()
						.returnResult(String.class)
						.getResponseBody();

		StepVerifier
				.create(responseBody)
				.expectNext("foo", "bar", "baz", "cux")
				.verifyComplete();

	}

}
