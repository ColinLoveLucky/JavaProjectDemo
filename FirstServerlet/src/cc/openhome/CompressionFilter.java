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
 * Servlet Filter implementation class CompressionFilter
 */
@WebFilter("/*")
public class CompressionFilter implements Filter {

	/**
	 * Default constructor.
	 */
	public CompressionFilter() {
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

		/*
		 * HttpServletRequest req = (HttpServletRequest) request;
		 * HttpServletResponse res = (HttpServletResponse) response; String
		 * encodings = req.getHeader("accept-encoding"); if ((encodings != null)
		 * && (encodings.indexOf("gzip") > -1)) { CompressionWrapper
		 * responseWrapper = new CompressionWrapper(res);
		 * responseWrapper.setHeader("content-encoding", "gzip");
		 * chain.doFilter(request, responseWrapper);
		 * 
		 * GZIPOutputStream gzipOutputStream =
		 * responseWrapper.getGzipOutputStream(); if (gzipOutputStream != null)
		 * { gzipOutputStream.finish(); } } else chain.doFilter(request,
		 * response);
		 */
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
