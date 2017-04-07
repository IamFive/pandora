package net.turnbig.pandora.web.taglib;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import net.turnbig.pandora.spring.SpringContextHolder;

public class DisplayTag extends TagSupport {

	private static final long serialVersionUID = 2275383657293444605L;

	private static final Logger logger = LoggerFactory.getLogger(DisplayTag.class);

	private String displayField;
	private String valueField;
	private String entity;
	private String value;

	private Map<String, Object> valueType = new HashMap<String, Object>();

	@Override
	public int doEndTag() throws JspException {
		try {
			StringBuilder sb = new StringBuilder("select {0} as output from {2} where {1} = ?");
			String sql = MessageFormat.format(sb.toString(), displayField, valueField, entity);

			DataSource datasource = SpringContextHolder.getBean("dataSource");

			JspWriter writor = this.pageContext.getOut();
			List<Map<String, Object>> list = getRecord(sql, datasource);
			if (list != null && list.size() > 0) {
				writor.write(list.get(0).get("output").toString());
			} else {
				writor.write("");
			}

		} catch (IOException e) {
			logger.warn("Error occur in diaply tag.", e);
		}

		return EVAL_PAGE;
	}

	/**
	 * @param sql
	 * @param datasource
	 * @return
	 */
	private List<Map<String, Object>> getRecord(String sql, DataSource datasource) {
		List<Map<String, Object>> list = null;
		String key = entity + "&" + valueField;
		if (!valueType.containsKey(key)) {
			try {
				if (StringUtils.isNumeric(value)) {
					list = new JdbcTemplate(datasource).queryForList(sql, Integer.parseInt(value));
				}
				valueType.put(key, Integer.class);
			} catch (Exception e) {
				list = new JdbcTemplate(datasource).queryForList(sql, value);
				valueType.put(key, String.class);
			}
		} else {
			Object param = value;
			Object type = valueType.get(key);
			if (type.equals(Integer.class)) {
				param = Integer.parseInt(value);
			}
			list = new JdbcTemplate(datasource).queryForList(sql, param);
		}

		return list;
	}

	/**
	 * @return the displayField
	 */
	public String getDisplayField() {
		return displayField;
	}

	/**
	 * @param displayField the displayField to set
	 */
	public void setDisplayField(String displayField) {
		this.displayField = displayField;
	}

	/**
	 * @return the valueField
	 */
	public String getValueField() {
		return valueField;
	}

	/**
	 * @param valueField the valueField to set
	 */
	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	/**
	 * @return the entity
	 */
	public String getEntity() {
		return entity;
	}

	/**
	 * @param entity the entity to set
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}


}
