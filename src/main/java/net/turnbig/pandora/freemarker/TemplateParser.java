/**
 * @(#)TemplateParser.java 2016年2月2日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.pandora.freemarker;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.turnbig.pandora.freemarker.XmlTemplateLoaderFactory.Templates;
import net.turnbig.pandora.freemarker.XmlTemplateLoaderFactory.Template;

/**
 * @author Woo Cupid
 * @date 2016年2月2日
 * @version $Revision$
 */
public class TemplateParser {

	private static final Logger logger = LoggerFactory.getLogger(TemplateParser.class);

	static JAXBContext ctx = null;

	public static JAXBContext getJaxbContext() {
		if(ctx == null) {
			try {
				ctx = JAXBContext.newInstance(Template.class, Templates.class);
			} catch (JAXBException e) {
				// should not happen
				logger.error("could not create sql templates jaxb context", e);
			}
		}
		
		return ctx;
	}

	public static Templates fromXML(String xml) {
		try {
			Unmarshaller um = getJaxbContext().createUnmarshaller();
			Templates unmarshal = (Templates) um.unmarshal(new StringReader(xml));
			return unmarshal;
		} catch (JAXBException e) {
			throw new RuntimeException("could not parse sql-template-xml", e);
		}
	}

	public static Templates fromXML(File f) {
		try {
			Unmarshaller um = getJaxbContext().createUnmarshaller();
			Templates unmarshal = (Templates) um.unmarshal(f);
			return unmarshal;
		} catch (JAXBException e) {
			throw new RuntimeException("could not parse sql-template-xml", e);
		}
	}

	public static Templates fromXML(InputStream is) {
		try {
			Unmarshaller um = getJaxbContext().createUnmarshaller();
			Templates unmarshal = (Templates) um.unmarshal(is);
			return unmarshal;
		} catch (JAXBException e) {
			throw new RuntimeException("could not parse sql-template-xml", e);
		}
	}

}
