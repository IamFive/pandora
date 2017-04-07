package net.turnbig.pandora.freemarker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import net.turnbig.pandora.freemarker.XmlTemplateLoaderFactory.TemplateLoader;

import freemarker.cache.StringTemplateLoader;

/**
 * 

<h3>xml based template factory for freemarker</h3>

spring xml configuration sample:
<pre>
<bean id="xmlTemplate" class="com.woo.jdbcx.sql.loader.SqlTemplateLoaderFactory.SqlTemplateLoader" >
	<property name="locations">
		<list>
			<value>classpath:/templates/</value>
			<value>classpath:/template2/sample.xml</value>
		</list>
	</property>
</bean>

<bean id="freemarkerConfigurer" class="org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean" 
	lazy-init="false">
	<property name="preTemplateLoaders">
		<list>
			<ref bean="xmlTemplate" />
		</list>
	</property>
	<property name="defaultEncoding" value="UTF-8" />
	<property name="freemarkerSettings">
		<props>
			<prop key="template_update_delay">0</prop>
		</props>
	</property>
</bean>
</pre>
 */
public class XmlTemplateLoaderFactory implements FactoryBean<TemplateLoader>, InitializingBean {

	private static final String TEMPLATE_SYNC_FOLDER = ".template.sync";
	private static Logger logger = LoggerFactory.getLogger(XmlTemplateLoaderFactory.class);

	private ResourceLoader resourceLoader = new DefaultResourceLoader();
	private String[] locations;
	private TemplateLoader sqlTemplateLoader = new TemplateLoader();
	private static String syncTemplateFolder;

	@Override
	public void afterPropertiesSet() throws Exception {
		createTemplateLoader();
	}

	/**
	 * @return 
	 * @throws IOException
	 */
	public TemplateLoader createTemplateLoader() throws IOException {
		// create sync sql folder
		syncTemplateFolder = MessageFormat.format("{0}{1}{2}",
				resourceLoader.getResource("/").getFile().getAbsolutePath(), File.separator, TEMPLATE_SYNC_FOLDER);
		new File(syncTemplateFolder).mkdirs();
		for (String path : locations) {
			loadTemplates(path);
		}
		return sqlTemplateLoader;
	}

	/**
	 * load templates from a special path,
	 * 
	 * <li>classpath:templates/template1.xml</li>
	 * <li>classpath:templates/</li>
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void loadTemplates(String path) throws IOException {
		Resource r = resourceLoader.getResource(path);
		if (r.exists()) {
			List<Template> templates = new ArrayList<Template>();
			try {
				templates = parseTemplate(r.getFile());
			} catch (Exception e) {
				if (path.endsWith(".xml")) {
					// ignore all not file path
					int idx = path.contains("/") ? path.lastIndexOf("/") : path.indexOf(":");
					String filename = path.substring(idx + 1);
					String syncToFileName = syncTemplateFolder + File.separator + filename;
					logger.debug("It seems {} is not a disk file, sync to {}", syncToFileName);
					InputStream is = r.getInputStream();
					IOUtils.copy(is, new FileOutputStream(new File(syncToFileName)));
					IOUtils.closeQuietly(is);
					templates = parseTemplate(new File(syncToFileName));
				}
			}

			for (Template xmlTemplate : templates) {
				sqlTemplateLoader.putTemplate(xmlTemplate.getName(), xmlTemplate.getTemplate(), xmlTemplate.getLastModified());
				sqlTemplateLoader.addMapper(sqlTemplateLoader.findTemplateSource(xmlTemplate.getName()), xmlTemplate.getTplFilePath());
			}
		}
	}

	/**
	 * when template file is in JAR-File, we can't get the File directly
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static List<Template> parseTemplate(InputStream is) throws IOException {
		String content = IOUtils.toString(is);
		logger.debug("It seems the template is in JAR file, copy it to {}", syncTemplateFolder);
		List<Template> list = new ArrayList<Template>();
		Templates templates = TemplateParser.fromXML(content);
		for (Template sqlTemplate : templates.getTemplates()) {
			sqlTemplate.setLastModified(new Date().getTime());
			sqlTemplate.setTplFilePath("");
			list.add(sqlTemplate);
		}
		return list;
	}

	public static List<Template> parseTemplate(File file) {
		List<Template> result = new ArrayList<Template>();
		if (file.isFile()) {
			logger.debug("load template from : {}", file.getAbsolutePath());
			Templates templates = TemplateParser.fromXML(file);
			for (Template sqlTemplate : templates.getTemplates()) {
				sqlTemplate.setLastModified(file.lastModified());
				sqlTemplate.setTplFilePath(file.getAbsolutePath());
				result.add(sqlTemplate);
			}
		} else if (file.isDirectory()) {
			logger.debug("load template from folder : {}", file.getAbsolutePath());
			File[] files = file.listFiles();
			for (File f : files) {
				result.addAll(parseTemplate(f));
			}
		}
		return result;
	}

	@Override
	public TemplateLoader getObject() throws Exception {
		return sqlTemplateLoader;
	}

	@Override
	public Class<TemplateLoader> getObjectType() {
		return TemplateLoader.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public String[] getLocations() {
		return locations;
	}

	public void setLocations(String[] locations) {
		this.locations = locations;
	}

	public TemplateLoader getSqlTemplateLoader() {
		return sqlTemplateLoader;
	}


	public static class TemplateLoader extends StringTemplateLoader {

		private HashMap<Object, String> resourceMapper = new HashMap<Object, String>();

		/*
		 * (non-Javadoc)
		 * 
		 * @see freemarker.cache.StringTemplateLoader#findTemplateSource(java.lang.String)
		 */
		@Override
		public Object findTemplateSource(String name) {
			// reload template
			Object stringTemplateSource = super.findTemplateSource(name);
			if (stringTemplateSource != null && resourceMapper.containsKey(stringTemplateSource)) {
				String path = resourceMapper.get(stringTemplateSource);
				List<Template> tpls = parseTemplate(new File(path));
				for (Template xmlTemplate : tpls) {
					putTemplate(xmlTemplate.getName(), xmlTemplate.getTemplate(), xmlTemplate.getLastModified());
					addMapper(super.findTemplateSource(name), xmlTemplate.getTplFilePath());
				}
			}
			return super.findTemplateSource(name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemarker.cache.StringTemplateLoader#getLastModified(java.lang.Object
		 * )
		 */
		@Override
		public long getLastModified(Object templateSource) {
			String path = resourceMapper.get(templateSource);
			File f = new File(path);
			return f.lastModified();
		}

		public void addMapper(Object object, String path) {
			this.resourceMapper.put(object, path);
		}

	}

	@XmlRootElement(name = "Templates")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Templates {

		@XmlElement(name = "Template")
		List<Template> templates = new ArrayList<Template>();

		/**
		 * @return the templates
		 */
		public List<Template> getTemplates() {
			return templates;
		}

		/**
		 * @param templates the templates to set
		 */
		public void setTemplates(List<Template> templates) {
			this.templates = templates;
		}


	}

	@XmlRootElement(name = "Template")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Template {

		@XmlElement(name = "name")
		private String name;
		@XmlElement(name = "template")
		private String template;
		private long lastModified;
		private String tplFilePath;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getTemplate() {
			return template;
		}

		public void setTemplate(String template) {
			this.template = template;
		}

		public long getLastModified() {
			return lastModified;
		}

		public void setLastModified(long lastModified) {
			this.lastModified = lastModified;
		}

		@Override
		public String toString() {
			return "XmlTemplate [name=" + name + ", template=" + template + ", lastModified=" + lastModified + "]";
		}

		/**
		 * @return the tplFilePath
		 */
		public String getTplFilePath() {
			return tplFilePath;
		}

		/**
		 * @param tplFilePath
		 *            the tplFilePath to set
		 */
		public void setTplFilePath(String tplFilePath) {
			this.tplFilePath = tplFilePath;
		}
	}
}
