package net.turnbig.pandora.web.taglib;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DictDisplayTag extends TagSupport {

	private static final long serialVersionUID = 2275383657293444605L;

	private static final Logger logger = LoggerFactory.getLogger(DictDisplayTag.class);

	private String value;
	private String key;

	@Override
	public int doEndTag() throws JspException {

		DictCache dictCache = DictCache.instance();

		HashMap<String, String> dict = dictCache.get(key);
		String display = dict != null ? dict.get(value) : "请先在dict中添加" + key;
		JspWriter writor = this.pageContext.getOut();

		try {
			writor.write(display != null ? display.toString() : "");
		} catch (IOException e) {
			logger.warn("Error occur in diaply dict tag.", e);
		}

		return EVAL_PAGE;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
