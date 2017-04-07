package net.turnbig.pandora.web.taglib;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

public class DictTag extends SelectTag {

	private static final Logger logger = LoggerFactory.getLogger(DictTag.class);

	private static final long serialVersionUID = 1L;
	protected String key;
	protected String start;
	protected String end;

	protected Map<String, String> getOptions() {
		DictCache dictCache = DictCache.instance();
		HashMap<String, String> values = dictCache.get(key);
		if (null == values || values.size() <= 0) {
			logger.warn("dict with key [{}] is not configurated", key);
			values = Maps.newHashMap();
		}

		Map<String, String> items = Maps.filterKeys(values, new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				boolean available = true;
				if (StringUtils.isNotBlank(start)) {
					available = available && (start.compareTo(input) <= 0);
				}
				if (StringUtils.isNotBlank(end)) {
					available = available && (end.compareTo(input) >= 0);
				}
				return available;
			}
		});

		return items;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

}
