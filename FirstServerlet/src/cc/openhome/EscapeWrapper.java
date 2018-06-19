package cc.openhome;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class EscapeWrapper extends HttpServletRequestWrapper {

	public EscapeWrapper(HttpServletRequest request) {
		super(request);
	}

	@Override
	public String getParameter(String name) {
		String value = getRequest().getParameter(name);
		return new StringEscapeUtils().escapeHtml(value);
	}

	class StringEscapeUtils {
		String escapeHtml(String value) {
			return value.trim();
		}
	}
}
