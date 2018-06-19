package cc.openhome;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class ServletContext
 *
 */
@WebListener
public class SomeContextListener implements ServletContextListener {

	/**
	 * Default constructor.
	 */
	public SomeContextListener() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent servletcontextevent) {
		// TODO Auto-generated method stub
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent servletcontextevent) {
		// ServletContext context = servletcontextevent.getServletContext();
		// context.getSessionCookieConfig().setName("colin-sessionId");
	}

}
