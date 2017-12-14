package net.turnbig.pandora.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.layout.font.FontProvider;

/**
 *
 * @author QianBiao.NG
 * @date   2017-11-13 10:30:09
 */
public class Html2Pdf {

	static final Logger logger = LoggerFactory.getLogger(Html2Pdf.class);

	static DefaultResourceLoader resourceLoader = new DefaultResourceLoader();

	static ConverterProperties properties;
	static FontProvider CJKFontProvider;

	/**
	 * get default converter properties 
	 * 	which provides CJK font support and use "UTF-8" as encoding
	 * 
	 * @return
	 */
	public static ConverterProperties getDefaultConverterProperties() {
		if (properties == null) {
			synchronized (resourceLoader) {
				if (properties == null) {
					properties = new ConverterProperties();
					properties.setFontProvider(getDefaultCJKFontProvider());
					properties.setCharset("UTF-8");
				}
			}
		}
		return properties;
	}

	/**
	 * get default CJK font provider which use 
	 * 
	 * <li> 1. PINGFANG-SC-LIGHT as chinese font type </li>
	 * <li> 2. malgun.ttf as kora font type </li>
	 * <li> 3. MSMINCHO.ttf as kora font type </li>
	 * 
	 * @return
	 */
	public static FontProvider getDefaultCJKFontProvider() {
		if (CJKFontProvider == null) {
			synchronized (resourceLoader) {
				if (CJKFontProvider == null) {
					try {
						CJKFontProvider = new FontProvider();
						Resource kora = resourceLoader.getResource("malgun.ttf");
						Resource japan = resourceLoader.getResource("msmincho.ttf");
						Resource chinese = resourceLoader.getResource("pingfang-sc-light.otf");
						CJKFontProvider.addFont(IOUtils.toByteArray(kora.getInputStream()));
						CJKFontProvider.addFont(IOUtils.toByteArray(japan.getInputStream()));
						CJKFontProvider.addFont(IOUtils.toByteArray(chinese.getInputStream()));
					} catch (IOException e) {
						logger.error("Failed to load pingfang sc light font type", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return CJKFontProvider;
	}

	/**
	 * convert HTML file to PDF file use default converter properties.
	 * 
	 * <p>Remember the converter will use the parent folder of HTML file as base-URI(resource context)</p>
	 * 
	 * @param htmlFile
	 * @param targetPdfFile
	 * @throws IOException
	 */
	public static void convert(File htmlFile, File targetPdfFile) throws IOException {
		HtmlConverter.convertToPdf(htmlFile, targetPdfFile, getDefaultConverterProperties());
	}

	public static void convert(File htmlFile, File targetPdfFile, String baseUri) throws IOException {
		ConverterProperties defaultConverterProperties = getDefaultConverterProperties();
		ConverterProperties copy = new ConverterProperties(defaultConverterProperties).setBaseUri(baseUri);
		HtmlConverter.convertToPdf(htmlFile, targetPdfFile, copy);
	}

	public static void convert(File htmlFile, File targetPdfFile, ConverterProperties properties) throws IOException {
		HtmlConverter.convertToPdf(htmlFile, targetPdfFile, properties);
	}

	public static void convert(String htmlContent, File targetPdfFile) throws IOException {
		HtmlConverter.convertToPdf(htmlContent, new FileOutputStream(targetPdfFile), getDefaultConverterProperties());
	}

	public static void convert(String htmlContent, File targetPdfFile, String baseUri) throws IOException {
		ConverterProperties defaultConverterProperties = getDefaultConverterProperties();
		ConverterProperties copy = new ConverterProperties(defaultConverterProperties).setBaseUri(baseUri);
		HtmlConverter.convertToPdf(htmlContent, new FileOutputStream(targetPdfFile), copy);
	}

	public static void convert(String htmlContent, File targetPdfFile, ConverterProperties properties)
			throws IOException {
		HtmlConverter.convertToPdf(htmlContent, new FileOutputStream(targetPdfFile), properties);
	}

	public static void main(String[] args) throws IOException {
		String contextFolder = "E:\\www\\express";
		String outputFileName = "QJ304501-0";
		File htmlFile = new File(contextFolder + File.separator + outputFileName + ".html");
		File pdfFile = new File(contextFolder + File.separator + outputFileName + ".pdf");
		Html2Pdf.convert(htmlFile, pdfFile);
	}
}
