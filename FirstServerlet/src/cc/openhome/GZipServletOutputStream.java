package cc.openhome;

import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

public class GZipServletOutputStream extends ServletOutputStream {

	private GZIPOutputStream gzipOutputStream;

	public GZipServletOutputStream(ServletOutputStream outputStream) throws IOException {
		this.gzipOutputStream = new GZIPOutputStream(outputStream);
	}

	@Override
	public void write(int i) throws IOException {
		gzipOutputStream.write(i);
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setWriteListener(WriteListener writelistener) {
		// TODO Auto-generated method stub

	}

	public GZIPOutputStream getGzipOutputStream() {
		return gzipOutputStream;
	}

}
