package cc.openhome;

import java.io.IOException;
import java.util.List;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AsyncNumServlet
 */
@WebServlet(name = "AsyncNumServlet", urlPatterns = { "/asyncNum.do" }, asyncSupported = true)
public class AsyncNumServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private List<AsyncContext> asyncs;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AsyncNumServlet() {
		super();
		// TODO Auto-generated constructor stub

	}

	@SuppressWarnings("unchecked")
	@Override
	public void init() throws ServletException {
		asyncs = (List<AsyncContext>) getServletContext().getAttribute("asyncs");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		AsyncContext ctx = request.startAsync();
		synchronized (asyncs) {
			asyncs.add(ctx);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
