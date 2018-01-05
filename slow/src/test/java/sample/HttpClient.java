package sample;

import org.springframework.util.StreamUtils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;



public class HttpClient {
	private final String baseUrl;

	public HttpClient(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public Request get(String url) throws IOException {
		return new Request().url(this.baseUrl + url);
	}

	public static class Request {
		private URL url;

		private Request() {
		}

		public Request url(String url) throws IOException {
			this.url = new URL(url);
			return this;
		}

		public Response exchange() {
			long delay = TimeUnit.SECONDS.toMillis(7);
			String request = "GET " + url.toExternalForm() + " HTTP/1.0\r\n";
			int port = url.getPort();
			request += "Accept: text/plain\r\n";
			request += "\r\n";
			Socket sock = null;
			StringBuilder result = new StringBuilder();
			try {
				sock = new Socket(InetAddress.getByName(url.getHost()), port > 0 ? port : url.getDefaultPort());
				OutputStream os = sock.getOutputStream();
				os.write(request.getBytes("UTF-8"));
				os.flush();


				InputStream is = sock.getInputStream();
				StringBuilder out = new StringBuilder();
				InputStreamReader reader = new InputStreamReader(is, Charset.defaultCharset());
				char[] buffer = new char[4096];

				int bytesRead;
				int reads = 0;
				while((bytesRead = reader.read(buffer)) != -1) {
					out.append(buffer, 0, bytesRead);
					if(++reads % 10 == 0) {
						System.out.println("Sleeping for " + delay);
						Thread.sleep(delay);
					}
				}

				return new Response(out.toString());
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				if (sock != null) {
					try {
						sock.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	public static class Response {
		private final String content;

		public Response(String content) {
			super();
			this.content = content;
		}

		public String getContent() {
			return this.content;
		}

		public Response print() {
			System.out.println(getContent());
			System.out.println(getContent().getBytes().length);
			return this;
		}
	}
}
