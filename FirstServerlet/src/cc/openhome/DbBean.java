package cc.openhome;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String jdbcUrl;
	private String userName;
	private String password;

	public DbBean() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	public boolean isConnectedOk() {
		boolean ok = false;
		Connection conn = null;
		SQLException ex = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl, userName, password);
			if (!conn.isClosed()) {
				ok = true;
			}
		} catch (SQLException e) {
			ex = e;
		} finally {
			if (conn != null) {
				try {
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
		return ok;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public void setUserName(String username) {
		this.userName = username;
	}
}
