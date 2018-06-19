package cc.openhome;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class HelloView
 */
@WebServlet("/hello.view")
public class HelloView extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String htmlTemplate = "<html>" + "<head>" + "<meta http-quuiv='Content-type'"
			+ " content='text/html;charset=UTF-8'>" + "<title>%s</title>" + "</head>" + "<body>" + "<h1>%s</h1>"
			+ "</body>" + "</html>";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HelloView() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String user = request.getParameter("user");
		String message = (String) request.getAttribute("message");
		String html = String.format(htmlTemplate, user, message);
		response.getWriter().println(html);
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
