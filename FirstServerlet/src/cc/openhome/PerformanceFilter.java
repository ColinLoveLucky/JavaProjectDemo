package cc.openhome;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 * Servlet Filter implementation class PerformanceFilter
 */
@WebFilter(filterName = "performance", urlPatterns = "/*")
public class PerformanceFilter implements Filter {

	// private FilterConfig config;

	/**
	 * Default constructor.
	 */
	public PerformanceFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		// place your code here

		// pass the request along the filter chain
		// long begin = System.currentTimeMillis();
		// chain.doFilter(request, response);
		// config.getServletContext()
		// .log(String.format("Request process %s millseconds",
		// System.currentTimeMillis() - begin));
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
		// config = fConfig;
	}

}
