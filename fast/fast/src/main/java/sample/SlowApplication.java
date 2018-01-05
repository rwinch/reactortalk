package sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SlowApplication {

	@GetMapping("/slow")
	public String slow() throws Exception {
		Thread.sleep(1000L);
		return "slow";
	}

	@GetMapping("/fast")
	public String fast() throws Exception {
		return "fast";
	}

	public static void main(String[] args) {
		SpringApplication.run(SlowApplication.class, args);
	}
}
