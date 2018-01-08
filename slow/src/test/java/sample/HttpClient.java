package sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;



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

		private long delay = TimeUnit.SECONDS.toMillis(7);

		private int delayEveryBytes = 100000;

		private Request() {
		}

		public Request url(String url) throws IOException {
			this.url = new URL(url);
			return this;
		}

		public Request delay(long delay) {
			this.delay = delay;
			return this;
		}

		public Request delayEveryBytes(int byteCount) {
			this.delayEveryBytes = byteCount;
			return this;
		}

		public Response exchange() {
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
				int totalBytesRead = 0;
				float status = 0;
				while((bytesRead = reader.read(buffer)) != -1) {
					totalBytesRead += bytesRead;
					out.append(buffer, 0, bytesRead);
					float newStatus = totalBytesRead / delayEveryBytes;
					if(status < newStatus) {
						System.out.print("Sleeping for " + delay + "...");
						System.out.flush();
						Thread.sleep(delay);
						System.out.println("Done");
						System.out.flush();
						status = newStatus;
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
