package cc.openhome;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DatabaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DataSource dataSource;

	public DatabaseBean() {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:comp/env");
			dataSource = (DataSource) envContext.lookup("jdbc/demo");

		} catch (NamingException ex) {
			throw new RuntimeException(ex);
		}
	}

	public boolean isConnectedOk() {
		boolean ok = false;
		Connection conn = null;
		SQLException ex = null;
		try {
			conn = dataSource.getConnection();
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
}
