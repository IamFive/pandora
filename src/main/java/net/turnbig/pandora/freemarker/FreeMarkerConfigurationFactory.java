package net.turnbig.pandora.freemarker;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;

/**
 * 
 * @author Woo Cupid
 * @date 2013年10月30日
 * @version $Revision$
 */
public class FreeMarkerConfigurationFactory extends FreeMarkerConfigurationFactoryBean {

	private Resource staticModelLocations;

	public Resource getStaticModelLocations() {
		return staticModelLocations;
	}

	public void setStaticModelLocations(Resource staticModelLocations) {
		this.staticModelLocations = staticModelLocations;
	}

	@Override
	public void afterPropertiesSet() throws IOException, TemplateException {
		super.afterPropertiesSet();
		if (this.staticModelLocations != null) {
			BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
			wrapper.setExposureLevel(BeansWrapper.EXPOSE_ALL);
			TemplateHashModel staticModels = wrapper.getStaticModels();

			Properties props = new Properties();
			PropertiesLoaderUtils.fillProperties(props, this.staticModelLocations);

			Enumeration<Object> en = props.keys();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				TemplateModel model = staticModels.get(props.getProperty(key));
				this.getObject().setSharedVariable(key, model);
			}
		}
	}
}
