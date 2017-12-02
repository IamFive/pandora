package net.turnbig.pandora.freemarker;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 *
 * @author QianBiao.NG
 * @date   2017-11-03 10:14:27
 */
public class FreemarkerRenderer {

	private static final Logger logger = LoggerFactory.getLogger(FreemarkerRenderer.class);
	static Configuration configuration = null;

	public static void init() {
		if (configuration == null) {
			// build template loader
			StringTemplateLoader templateLoader = new StringTemplateLoader();

			// build configuration
			Configuration config = new Configuration(Configuration.getVersion());
			config.setTemplateLoader(templateLoader);
			config.setTemplateUpdateDelayMilliseconds(Long.MAX_VALUE);
			config.setDefaultEncoding("UTF-8");
			configuration = config;
		}
	}

	/**
	 * convert string content into Freemarker template and render it
	 * 
	 * @param templateContent 
	 * @param model
	 * @return
	 */
	public static String processTplContent(String templateContent, Object model) {
		try {
			if (templateContent != null) {
				// no-cache here
				Template template = new Template(RandomStringUtils.random(8), new StringReader(templateContent),
						configuration);
				return processTpl(template, model);
			}
			return null;
		} catch (IOException e) {
			logger.error("Can not get freemarker template resource", e);
			throw new RuntimeException(e);
		} catch (TemplateException e) {
			logger.error("There got a grammar error in freemarker template :" + templateContent, e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * process template to string
	 * 
	 * @param template
	 * @param model
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String processTpl(Template template, Object model) throws IOException, TemplateException {
		init();
		StringWriter result = new StringWriter();
		template.process(model, result);
		return result.toString();
	}

}
