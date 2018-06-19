package cc.openhome;

import java.io.Serializable;
import java.sql.Timestamp;

public class File implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String filename;
	private Timestamp savedTime;
	private byte[] bytes;

	public long getId() {
		return id;
	}

	public void setId(long l) {
		this.id = l;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Timestamp getSavedTime() {
		return savedTime;
	}

	public void setSavedTime(Timestamp savedTime) {
		this.savedTime = savedTime;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

}
