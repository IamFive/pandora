/**
 * @(#)SelectTag2.java 2016年3月31日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.pandora.web.taglib;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.jsp.JspException;
import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.tags.form.AbstractHtmlInputElementTag;
import org.springframework.web.servlet.tags.form.TagWriter;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.turnbig.pandora.spring.SpringContextHolder;

/**
 * @author Woo Cupid
 * @date 2016年3月31日
 * @version $Revision$
 */
public class SelectTag extends AbstractHtmlInputElementTag {

	private static final String defaultBlankDisplayTxt = "-- 请选择 --";
	protected static final long serialVersionUID = -85148664789201973L;

	protected String id;
	protected String name;
	protected String displayField;
	protected String valueField;
	protected String entity;
	protected String condition;
	protected String style;
	protected String value;
	protected String blank;
	protected String blankTxt;

	// 用于 option group - 最多只支持二级?
	protected String groupby;
	protected String orderby;
	protected String multiple;

	protected Boolean _selected = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.servlet.tags.form.AbstractFormTag#writeTagContent(org.springframework.web.servlet.tags.
	 * form.TagWriter)
	 */
	@Override
	protected int writeTagContent(TagWriter tagWriter) throws JspException {
		tagWriter.startTag("select");
		writeOptionalAttributes(tagWriter);

		if (Boolean.valueOf(this.getBlank())) {
			renderOption(tagWriter, "",
					StringUtils.isEmpty(this.getBlankTxt()) ? defaultBlankDisplayTxt : this.getBlankTxt());
		}

		// normal select
		if (StringUtils.isEmpty(groupby)) {
			Map<String, String> options = getOptions();
			for (Entry<String, String> option : options.entrySet()) {
				renderOption(tagWriter, option.getKey(), option.getValue());
			}
		} else { // select with option group
			Collection<SelectEntry> grouped = getGroupedOptions();
			for (SelectEntry entry : grouped) {
				renderOptionGroup(tagWriter, entry);
				List<SelectEntry> children = entry.getChildren();
				for (SelectEntry child : children) {
					renderOption(tagWriter, child.getValue(), child.getDisplay());
				}
			}
		}

		tagWriter.endTag(true);
		return SKIP_BODY;
	}

	/**
	 * @param tagWriter
	 * @param entry
	 * @throws JspException 
	 */
	private void renderOptionGroup(TagWriter tagWriter, SelectEntry entry) throws JspException {
		String labelDisplayString = HtmlUtils.htmlEscape(entry.getDisplay(), "UTF-8");
		tagWriter.startTag("optgroup");
		tagWriter.writeAttribute("label", labelDisplayString);
		tagWriter.endTag();
	}

	/**
	 * @return 
	 * 
	 */
	protected Collection<SelectEntry> getGroupedOptions() {
		StringBuilder sb = new StringBuilder("select {0} as display, {1} as value, {2} as grouped from {3}");
		if (!StringUtils.isEmpty(condition)) {
			sb.append(" where ").append(condition);
		}

		if (!StringUtils.isEmpty(orderby)) {
			sb.append(" order by ").append(orderby);
		}

		String sql = MessageFormat.format(sb.toString(), displayField, valueField, groupby, entity);
		DataSource datasource = SpringContextHolder.getBean("dataSource");

		List<SelectEntry> list = new JdbcTemplate(datasource).query(sql,
				new BeanPropertyRowMapper<SelectEntry>(SelectEntry.class));

		Map<String, SelectEntry> mapped = new LinkedHashMap<String, SelectEntry>();
		for (SelectEntry entry : list) {
			mapped.put(entry.getValue(), entry);
		}

		Map<String, SelectEntry> grouped = new LinkedHashMap<String, SelectEntry>();
		for (SelectEntry entry : list) {
			String key = entry.getGrouped();
			if (mapped.containsKey(key)) {
				SelectEntry groupedEntry = mapped.get(key);
				groupedEntry.addChildren(entry);
				grouped.put(key, groupedEntry);
			}
		}

		return grouped.values();
	}

	/**
	 * write HTML standard attributes
	 * 
	 * @param tagWriter
	 * @throws JspException
	 */
	protected void writeOptionalAttributes(TagWriter tagWriter) throws JspException {
		tagWriter.writeOptionalAttributeValue("id", this.getId());
		tagWriter.writeOptionalAttributeValue("name", this.getName());
		tagWriter.writeOptionalAttributeValue("multiple", getMultiple());
		super.writeOptionalAttributes(tagWriter);
	}

	private void renderOption(TagWriter tagWriter, String value, String label) throws JspException {
		String valueDisplayString = HtmlUtils.htmlEscape(value.toString(), "UTF-8");
		String labelDisplayString = HtmlUtils.htmlEscape(label.toString(), "UTF-8");
		tagWriter.startTag("option");
		tagWriter.writeAttribute("value", valueDisplayString);
		if (this.value != null) {
			if (this.value.equalsIgnoreCase(value)) {
				tagWriter.writeAttribute("selected", "selected");
			}
		} else if (!_selected) {
			tagWriter.writeAttribute("selected", "selected");
			_selected = true;
		}
		tagWriter.appendValue(labelDisplayString);
		tagWriter.endTag();
	}

	/**
	 * @return 
	 * 
	 */

	protected Map<String, String> getOptions() {
		StringBuilder sb = new StringBuilder("select {0} as display, {1} as value_ from {2}");
		if (!StringUtils.isEmpty(condition)) {
			sb.append(" where ").append(condition);
		}
		if (!StringUtils.isEmpty(orderby)) {
			sb.append(" order by ").append(orderby);
		}

		String sql = MessageFormat.format(sb.toString(), displayField, valueField, entity);
		DataSource datasource = SpringContextHolder.getBean("dataSource");
		List<Map<String, Object>> options = new JdbcTemplate(datasource).queryForList(sql);

		LinkedHashMap<String, String> result = Maps.newLinkedHashMap();
		for (Map<String, Object> option : options) {
			result.put(option.get("value_").toString(), option.get("display").toString());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.tags.form.AbstractHtmlElementTag#resolveCssClass()
	 */
	@Override
	protected String resolveCssClass() throws JspException {
		return ObjectUtils.getDisplayString(evaluate("cssClass", getCssClass()));
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the condition
	 */
	public String getCondition() {
		return condition;
	}

	/**
	 * @param condition the condition to set
	 */
	public void setCondition(String condition) {
		this.condition = condition;
	}

	/**
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * @param style the style to set
	 */
	public void setStyle(String style) {
		this.style = style;
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

	/**
	 * @return the blank
	 */
	public String getBlank() {
		return blank;
	}

	/**
	 * @param blank the blank to set
	 */
	public void setBlank(String blank) {
		this.blank = blank;
	}

	/**
	 * @return the blankTxt
	 */
	public String getBlankTxt() {
		return blankTxt;
	}

	/**
	 * @param blankTxt the blankTxt to set
	 */
	public void setBlankTxt(String blankTxt) {
		this.blankTxt = blankTxt;
	}

	/**
	 * @return the groupby
	 */
	public String getGroupby() {
		return groupby;
	}

	/**
	 * @param groupby the groupby to set
	 */
	public void setGroupby(String groupby) {
		this.groupby = groupby;
	}

	/**
	 * @return the orderby
	 */
	public String getOrderby() {
		return orderby;
	}

	/**
	 * @param orderby the orderby to set
	 */
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	/**
	 * @return the multiple
	 */
	public String getMultiple() {
		return multiple;
	}

	/**
	 * @param multiple the multiple to set
	 */
	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}

	public static class SelectEntry {

		private String value;
		private String display;
		private String grouped;

		private List<SelectEntry> children;

		/**
		 * @return the children
		 */
		public List<SelectEntry> getChildren() {
			return children;
		}

		/**
		 * @param children the children to set
		 */
		public void addChildren(SelectEntry entry) {
			if (this.children == null) {
				this.children = Lists.newArrayList();
			}

			this.children.add(entry);
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

		/**
		 * @return the display
		 */
		public String getDisplay() {
			return display;
		}

		/**
		 * @param display the display to set
		 */
		public void setDisplay(String display) {
			this.display = display;
		}

		/**
		 * @return the grouped
		 */
		public String getGrouped() {
			return grouped;
		}

		/**
		 * @param grouped the grouped to set
		 */
		public void setGrouped(String grouped) {
			this.grouped = grouped;
		}

	}

}
