package cc.openhome;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Statement;

public class GuestBookBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String jdbcUrl = "jdbc:mysql://172.16.36.45:3306/Test";
	private String username = "root";
	private String password = "WangHua1986";

	public GuestBookBean() {
		try {
			Class.forName("com.mysql.jdbc.Driver");

		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void setMessage(Message message) {

		Connection conn = null;
		Statement statement = null;
		SQLException ex = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl, username, password);
			statement = (Statement) conn.createStatement();
			statement.executeUpdate(String.format("INSERT INTO t_message(name,email,msg) values('%s','%s','%s')",
					message.getName(), message.getEmail(), message.getMsg()));

		} catch (SQLException e) {
			ex = e;
		} finally {

			if (statement != null) {
				try {
					statement.close();
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

	public List<Message> getMessages() {
		Connection conn = null;
		Statement statement = null;
		ResultSet result = null;
		SQLException ex = null;
		List<Message> messages = null;
		try {

			conn = DriverManager.getConnection(jdbcUrl, username, password);
			statement = (Statement) conn.createStatement();
			result = statement.executeQuery("SELECT * FROM t_message");
			messages = new ArrayList<Message>();
			Message message = null;
			while (result.next()) {
				message = new Message();
				message.setId(result.getLong(1));
				message.setName(result.getString(2));
				message.setEmail(result.getString(3));
				message.setMsg(result.getString(4));
				messages.add(message);
			}
		} catch (SQLException e) {
			ex = e;
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					if (ex == null) {
						ex = e;
					}
				}
			}
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

		return messages;
	}

}
