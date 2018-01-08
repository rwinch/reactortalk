package sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

@SpringBootApplication
@RestController
public class SlowApplication {

	@GetMapping("/slow")
	public String slow() throws Exception {
		Thread.sleep(TimeUnit.SECONDS.toMillis(10));
		return "slow";
	}

	@GetMapping(path = "/fast")
	public String fast() throws Exception {
		String FAST = "fast\n";
		String result = FAST;
		for(int i=0;i<20000;i++) {
			result += FAST;
		}
		return result;
	}


	@GetMapping(path = "/servlet")
	public void servlet(HttpServletResponse response) throws Exception {
		PrintWriter writer = response.getWriter();
		String FAST = "fast\n";
		String result = FAST;
		response.setBufferSize(1);
		for(int i=0;i<20000;i++) {
			//			result += FAST;
			writer.write(FAST);
			response.flushBuffer();
		}
		int bufferSize = response.getBufferSize();
		System.out.println(bufferSize);
		//		return result;
	}

	public static void main(String[] args) {
		SpringApplication.run(SlowApplication.class, args);
	}

	static class Message {
		final String content;

		Message(String content) {
			this.content = content;
		}

		public String getContent() {
			return this.content;
		}
	}


	@Configuration
	static class MyWebConfig {

		@Bean
		public JettyEmbeddedServletContainerFactory serverFactory() {
			JettyEmbeddedServletContainerFactory factory = new JettyEmbeddedServletContainerFactory();
			factory.setAcceptors(1);
			factory.setSelectors(1);
			factory.setThreadPool(new QueuedThreadPool(4, 3, 60000, new BlockingArrayQueue<>()));
			return factory;
		}
	}

}
