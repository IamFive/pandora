package net.turnbig.pandora.email;

import java.io.File;
import java.io.IOException;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;

/**
 *
 * @author QianBiao.NG
 * @date   2017-11-07 14:25:12
 */
public class Html2PdfUtil {

	public static void main(String[] args) throws IOException  {
		ConverterProperties prop = new ConverterProperties();
		DefaultFontProvider fontProvider = new DefaultFontProvider(true, true, true);
		prop.setFontProvider(fontProvider);
		HtmlConverter.convertToPdf(new File("E:\\www\\express\\QJ292793.html"), new File("E:\\www\\express\\QJ292793.pdf"), prop);
	}

}
