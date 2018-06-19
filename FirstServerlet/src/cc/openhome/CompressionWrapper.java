package cc.openhome;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class CompressionWrapper extends HttpServletResponseWrapper {

	private GZipServletOutputStream gzipOutputStream;
	private PrintWriter printWriter;

	public CompressionWrapper(HttpServletResponse response) {
		super(response);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (printWriter != null) {
			throw new IllegalStateException();
		}
		if (gzipOutputStream == null) {
			gzipOutputStream = new GZipServletOutputStream(getResponse().getOutputStream());
		}
		return gzipOutputStream;
	}

	public PrintWriter getWaiter() throws IOException {
		if (gzipOutputStream != null) {
			throw new IllegalStateException();
		}
		if (printWriter == null) {
			gzipOutputStream = new GZipServletOutputStream(getResponse().getOutputStream());
			OutputStreamWriter osw = new OutputStreamWriter(gzipOutputStream, getResponse().getCharacterEncoding());
			printWriter = new PrintWriter(osw);
		}
		return printWriter;
	}

	@Override
	public void setContentLength(int len) {
	}

	public GZIPOutputStream getGzipOutputStream() {
		if (this.gzipOutputStream == null)
			return null;
		return this.gzipOutputStream.getGzipOutputStream();
	}
}
