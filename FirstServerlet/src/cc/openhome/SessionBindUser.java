package cc.openhome;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

/**
 * Application Lifecycle Listener implementation class SessionBindUser
 *
 */
@WebListener
public class SessionBindUser implements HttpSessionBindingListener {

	private String name;
	private String data;

	public SessionBindUser(String name) {
		this.name = name;
	}

	/**
	 * Default constructor.
	 */
	public SessionBindUser() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpSessionBindingListener#valueBound(HttpSessionBindingEvent)
	 */
	public void valueBound(HttpSessionBindingEvent httpsessionbindingevent) {
		// TODO Auto-generated method stub
		this.setData(String.format("%s From the database", name));
	}

	/**
	 * @see HttpSessionBindingListener#valueUnbound(HttpSessionBindingEvent)
	 */
	public void valueUnbound(HttpSessionBindingEvent httpsessionbindingevent) {
		// TODO Auto-generated method stub
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
