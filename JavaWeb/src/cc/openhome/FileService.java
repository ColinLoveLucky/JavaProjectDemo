package cc.openhome;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class FileService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DataSource dataSource;
	private Connection conn = null;
	private PreparedStatement statement = null;
	private ResultSet result = null;
	private SQLException ex = null;

	public FileService() {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			dataSource = (DataSource) envContext.lookup("jdbc/demo");
		} catch (NamingException ex) {
			throw new RuntimeException(ex);
		}
	}

	public File getFile(File file) {
		try {
			conn = dataSource.getConnection();
			statement = conn.prepareStatement("SELECT filename,bytes FROM t_files where id=?");
			statement.setLong(1, file.getId());
			result = statement.executeQuery();
			while (result.next()) {
				file = new File();
				file.setFilename(result.getString(1));
				file.setBytes(result.getBytes(2));
			}
		} catch (SQLException e) {
			ex = e;
		} finally {
			if (statement != null || conn != null) {
				try {
					if (statement != null)
						statement.close();
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					if (ex == null) {
						ex = e;
					}
				}
			}
			if (ex != null) {
				throw new RuntimeException(ex);
			}
		}

		return file;

	}

	public List<File> getFileList() {
		List<File> fileList = null;
		try {
			conn = dataSource.getConnection();
			statement = conn.prepareStatement("SELECT id,filename,savedTime FROM t_files");
			result = statement.executeQuery();
			fileList = new ArrayList<File>();

			while (result.next()) {
				File file = new File();
				file.setId(result.getLong(1));
				file.setFilename(result.getString(2));
				file.setSavedTime(result.getTimestamp(3));
				fileList.add(file);
			}

		} catch (SQLException e) {
			ex = e;
		} finally {
			if (statement != null || conn != null) {
				try {
					if (statement != null)
						statement.close();
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					if (ex == null) {
						ex = e;
					}
				}
			}
			if (ex != null) {
				throw new RuntimeException(ex);
			}
		}
		return fileList;
	}

	public void Save(File file) {
		try {
			conn = dataSource.getConnection();
			statement = conn.prepareStatement("INSERT INTO t_files(filename,savedTime,bytes) values(?,?,?)");
			statement.setString(1, file.getFilename());
			statement.setTimestamp(2, file.getSavedTime());
			statement.setBytes(3, file.getBytes());
			statement.executeUpdate();
		} catch (SQLException e) {
			ex = e;

		} finally {
			if (statement != null || conn != null) {
				try {
					if (statement != null)
						statement.close();
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					if (ex == null) {
						ex = e;
					}
				}
			}
			if (ex != null) {
				throw new RuntimeException(ex);
			}
		}

	}

	public void delete(File file) {
		try {
			conn = dataSource.getConnection();
			statement = conn.prepareStatement("DELETE FROM t_files WHERE id=?");
			statement.setLong(1, file.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			ex = e;
		} finally {
			if (statement != null || conn != null) {
				try {
					if (statement != null)
						statement.close();
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					if (ex == null) {
						ex = e;
					}
				}
			}
			if (ex != null) {
				throw new RuntimeException(ex);

			}
		}
	}

}
