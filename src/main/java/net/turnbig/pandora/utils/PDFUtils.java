package net.turnbig.pandora.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.Lists;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

/**
 *
 * @author QianBiao.NG
 * @date   2017-11-13 14:56:28
 */
public class PDFUtils {

	static final MemoryUsageSetting MEMORY_USAGE = MemoryUsageSetting.setupMixed(3 * 1024 * 1024);

	/**
	 * merge PDF files
	 * 
	 * @param sourceFiles 	source PDF file path list
	 * @param targetFile	target PDF file path
	 * @throws IOException
	 */
	public static void mergeFiles(List<String> sourceFiles, String targetFile, PDDocumentInformation docInfo)
			throws IOException {
		FileUtils.forceMkdirParent(new File(targetFile));
		PDFMergerUtility merger = new PDFMergerUtility();
		for (String sourceFile : sourceFiles) {
			merger.addSource(sourceFile);
		}
		merger.setDestinationDocumentInformation(docInfo);
		merger.setDestinationFileName(targetFile);
		merger.mergeDocuments(MEMORY_USAGE);// 1M ?
	}

	/**
	 * merge PDF files
	 * 
	 * @param sourceFiles	source PDF files
	 * @param targetFile	target PDF file
	 * @throws IOException
	 */
	public static void mergeFiles(List<File> sourceFiles, File targetFile, PDDocumentInformation docInfo)
			throws IOException {
		FileUtils.forceMkdirParent(targetFile);
		PDFMergerUtility merger = new PDFMergerUtility();
		for (File sourceFile : sourceFiles) {
			merger.addSource(sourceFile);
		}
		merger.setDestinationDocumentInformation(docInfo);
		merger.setDestinationStream(new FileOutputStream(targetFile));
		merger.mergeDocuments(MEMORY_USAGE);// 1M ?
	}

	public static void mergeHttpRemoteFiles(List<String> urls, File targetFile, PDDocumentInformation docInfo)
			throws IOException {
		FileUtils.forceMkdirParent(targetFile);
		PDFMergerUtility merger = new PDFMergerUtility();
		for (String url : urls) {
			CloseableHttpClient http = HttpClientFactory.create().build();
			CloseableHttpResponse execute = http.execute(RequestBuilder.get(url).build());
			merger.addSource(execute.getEntity().getContent());
		}

		merger.setDestinationDocumentInformation(docInfo);
		merger.setDestinationStream(new FileOutputStream(targetFile));
		merger.mergeDocuments(MEMORY_USAGE);// 1M ?
	}

	public static void mergeHttpRemoteFiles(List<String> urls, OutputStream targetOutputStream,
			PDDocumentInformation docInfo) throws IOException {
		PDFMergerUtility merger = new PDFMergerUtility();
		for (String url : urls) {
			CloseableHttpClient http = HttpClientFactory.create().build();
			CloseableHttpResponse execute = http.execute(RequestBuilder.get(Encodes.uriEncode(url)).build());
			merger.addSource(execute.getEntity().getContent());
		}

		merger.setDestinationDocumentInformation(docInfo);
		merger.setDestinationStream(targetOutputStream);
		merger.mergeDocuments(MEMORY_USAGE);// 1M ?
	}

	/**
	 * @return
	 */
	public static PDDocumentInformation buildDocInfo(String author, String producer) {
		PDDocumentInformation info = new PDDocumentInformation();
		info.setAuthor(author);
		info.setProducer(producer);
		info.setCreationDate(Calendar.getInstance(Locale.CHINA));
		return info;
	}

	public static void main(String[] args) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PDFUtils.mergeHttpRemoteFiles(
				Lists.newArrayList(
						"http://l1-1252321162.cossh.myqcloud.com/order/voucher/ArVqwyFODHgWpsiMFHRRckisAFeUaY.pdf"),
				bos,
				buildDocInfo("jarvis@turnbig.net", "www.turnbig.net"));

		FileUtils.writeByteArrayToFile(new File("6.pdf"), bos.toByteArray());
	}

}
