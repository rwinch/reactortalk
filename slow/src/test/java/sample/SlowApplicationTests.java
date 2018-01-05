package sample;

import org.junit.Test;
import org.springframework.util.StreamUtils;

import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

public class SlowApplicationTests {
	int port = 8080;

	@Test
	public void slowConnection() throws Exception {
		HttpClient client = new HttpClient("http://localhost:"+ this.port);
		client.get("/fast").exchange().print();
	}

	@Test
	public void slowServletConnection() throws Exception {
		HttpClient client = new HttpClient("http://localhost:"+ this.port);
		client.get("/servlet").exchange().print();
	}

	@Test
	public void normalConnection() throws Exception {
		URL url = new URL("http://localhost:" + this.port + "/fast");
		URLConnection conn = url.openConnection();
		System.out.println(StreamUtils.copyToString(conn.getInputStream(), Charset.defaultCharset()));
	}
}
