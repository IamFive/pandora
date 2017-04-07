package net.turnbig.pandora.freemarker;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.turnbig.pandora.spring.SpringContextHolder;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 
 * @author Woo Cupid
 * @date 2013年10月30日
 * @version $Revision$
 */
public class FreeMarkerHelper {

	private static Logger logger = LoggerFactory.getLogger(FreeMarkerHelper.class);

	private static Configuration configuration = SpringContextHolder.getBean("freemarkerConfigurer");

	public static Template getTemplate(String name) {
		try {
			return configuration.getTemplate(name);
		} catch (IOException e) {
			logger.error("Can not get freemarker template resource", e);
			throw new RuntimeException(e);
		}
	}

	public static String getTplContent(String templateName) {
		try {
			Template template = FreeMarkerHelper.getTemplate(templateName);
			String result = processTemplateIntoString(template, null);
			return result.trim();
		} catch (IOException e) {
			logger.error("Can not get freemarker template resource", e);
			throw new RuntimeException(e);
		} catch (TemplateException e) {
			logger.error("There got a grammar error in freemarker template " + templateName, e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * It's not the same as {@link FreeMarkerHelper#getTplContent(String)}; </br>
	 * The content return by this method does not contain <code>'\r' '\n' '\t'</code> which is more "like" a SQL.
	 * But i do nothing about duplicate spaces, Because it's hard to separate useless spaces.
	 * 
	 * @param templateName
	 * @return
	 *         public String getSqlTplContent(String templateName) {
	 *         String result = this.getTplContent(templateName);
	 *         return result.replaceAll("[\\r|\\n|\\t]", "");
	 *         }
	 */
	public static String process(String templateName, Object model) {
		try {
			Template template = FreeMarkerHelper.getTemplate(templateName);
			return processTemplateIntoString(template, model);
		} catch (IOException e) {
			logger.error("Can not get freemarker template resource", e);
			throw new RuntimeException(e);
		} catch (TemplateException e) {
			logger.error("There got a grammar error in freemarker template " + templateName, e);
			throw new RuntimeException(e);
		}
	}

	public static void process(String templateName, Object model, Writer writer) {
		try {
			Template template = FreeMarkerHelper.getTemplate(templateName);
			template.process(model, writer);
		} catch (IOException e) {
			logger.error("Can not get freemarker template resource", e);
			throw new RuntimeException(e);
		} catch (TemplateException e) {
			logger.error("There got a grammar error in freemarker template " + templateName, e);
			throw new RuntimeException(e);
		}
	}

	public static void processTemplateIntoString(String tplPath, Object model, Writer writer) {
		try {
			Template template = FreeMarkerHelper.getTemplate(tplPath);
			template.process(model, writer);
		} catch (IOException e) {
			logger.error("Error accour on reading freemarker template resource:" + tplPath, e);
			throw new RuntimeException(e);
		} catch (TemplateException e) {
			logger.error("There got a grammar error in freemarker template " + tplPath, e);
			throw new RuntimeException(e);
		}
	}

	public static String processTemplateIntoString(Template template, Object model)
			throws IOException, TemplateException {
		StringWriter result = new StringWriter();
		template.process(model, result);
		return result.toString();
	}

}
