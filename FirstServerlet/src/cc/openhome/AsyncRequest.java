package cc.openhome;

import java.io.PrintWriter;

import javax.servlet.AsyncContext;

public class AsyncRequest implements Runnable {

	private AsyncContext ctx;

	public AsyncRequest(AsyncContext ctx) {
		this.ctx = ctx;
	}

	public void run() {
		try {
			Thread.sleep(10000);
			PrintWriter out = ctx.getResponse().getWriter();
			out.println("Waiting .....XD");
			ctx.complete();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
