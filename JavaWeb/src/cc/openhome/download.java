package cc.openhome;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class download
 */
@WebServlet("/download.do")
public class download extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public download() {
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
		String id = request.getParameter("id");
		File file = new File();
		file.setId(Long.parseLong(id));
		FileService fileService = (FileService) getServletContext().getAttribute("fileService");
		file = fileService.getFile(file);
		String filename = null;
		if (request.getHeader("User-Agent").contains("MSIE")) {
			filename = URLEncoder.encode(file.getFilename(), "UTF-8");
		} else {
			filename = new String(file.getFilename().getBytes("UTF-8"), "ISO-8859-1");

		}
		response.setContentType("application/octet-stream");
		response.setHeader("content-disposition", "attachment;filename=\"" + filename + "\"");
		OutputStream out = response.getOutputStream();
		out.write(file.getBytes());
		out.close();

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
