package com.mboot.crawler.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XmlSerializer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.openhtmltopdf.bidi.support.ICUBidiReorderer;
import com.openhtmltopdf.bidi.support.ICUBidiSplitter;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.TextDirection;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PDFParserService {

	PdfRendererBuilder builder = new PdfRendererBuilder();
	HtmlCleaner cleaner = new HtmlCleaner();
	XmlSerializer xmlSerializer;

	@PostConstruct
	private void init() throws IOException {

		System.setProperty("wagon.http.ssl.insecure", "true");
		System.setProperty("wagon.http.ssl.allowall", "true");
		System.setProperty("wagon.http.ssl.ignore.validity.dates", "true");
//		System.setProperty("","");
		builder.useFont(stream2file(new ClassPathResource("fonts/AlinmaTheSans-Bold.ttf").getInputStream()), "Arial");
		builder.useFont(stream2file(new ClassPathResource("fonts/arial.ttf").getInputStream()), "Arial");
		builder.useFont(stream2file(new ClassPathResource("fonts/SansSerif-Italic.ttf").getInputStream()),
				"sans-serif");
		builder.useFont(stream2file(new ClassPathResource("fonts/Tahoma.ttf").getInputStream()), "Tahoma");
		builder.useFont(stream2file(new ClassPathResource("fonts/TIMES.TTF").getInputStream()), "Times New Roman");
		builder.useFastMode();
//		builder.setReplacedElementFactory(new B64ImgReplacedElementFactory());

		builder.useUnicodeBidiSplitter(new ICUBidiSplitter.ICUBidiSplitterFactory());
		builder.useUnicodeBidiReorderer(new ICUBidiReorderer());
		builder.defaultTextDirection(TextDirection.LTR); // OR RTL
		log.info("Done Intialized PDFParser ...");

		CleanerProperties cleanerProperties = cleaner.getProperties();
		cleanerProperties.setUseCdataForScriptAndStyle(false);
		cleanerProperties.setUseEmptyElementTags(false);
		cleanerProperties.setRecognizeUnicodeChars(true);
		xmlSerializer = new PrettyXmlSerializer(cleanerProperties);

		log.info("Done Intialized Cleaner ...");
	}

	public void parseTemplate(String content, OutputStream outputStream) throws Exception {
//		response.setContentType("application/pdf");
//		response.setHeader("Content-Disposition", "attachment;filename=" + cTemplate.getTemplateName() + ".pdf");
//		response.setStatus(HttpServletResponse.SC_OK);

//		new OutputStreamWriter(
//			    new FileOutputStream(cTemplate.getTemplateName() + ".pdf"), "UTF-8");
//		ITextRenderer renderer = new ITextRenderer();
//		renderer.getFontResolver().addFont("/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//		renderer.getFontResolver().addFont("/fonts/ArialBold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//		renderer.getFontResolver().addFont("/fonts/ArialUnicodeMS.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//		renderer.getFontResolver().addFont("/fonts/ARIALUNI.TTF", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//		renderer.getFontResolver().addFont("/fonts/Cardo-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//		renderer.getFontResolver().addFont("/fonts/FreeSans.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//		renderer.getFontResolver().addFont("/fonts/PlayfairDisplay-Regular.ttf", BaseFont.IDENTITY_H,
//				BaseFont.EMBEDDED);
//		renderer.getFontResolver().addFont("/fonts/PT_Sans-Web-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

//	        renderer.getFontResolver().addFont("/fonts/arialbold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//		renderer.getSharedContext().setReplacedElementFactory(new B64ImgReplacedElementFactory());

//		ITextFontResolver fontResolver = renderer.getFontResolver();
//		fontResolver.
//		String style = getTagValues(cTemplate.getTemplateRendered());
//		String t = "<?xml version='1.0' encoding='utf-8'?><html><head>"
//				+ "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>"
////				+ "<link rel=\"stylesheet\" href=\"jodit-angular.css\"/>"
//				+ "<style>" + style
////                + readCss()
////				+ "blockquote {" + " border-left: 2px solid #222;" + "    margin-left: 0;"
////				+ "    padding-left: 5px;" + "    color: #222 " + "}" + "blockquote:before {" +"}"
////				+ "html, body {" + " margin: 0;" + " padding: 0;" + " font-family: Arial;" + " font-size: 10px;"
////				+ " line-height: 14px;" + "}" + "blockquote p {" + "  display: inline;" + "}"
//				+ "</style></head><body>" + cTemplate.getTemplateRendered().replace(style, "") + "</body></html>";

		TagNode rootTagNode = cleaner.clean(content);

		content = xmlSerializer.getAsString(rootTagNode, "ISO-8859-1");

//		System.out.println(t);
		builder.withHtmlContent(content, "");
		builder.toStream(outputStream);
		builder.run();

	}

	private static final Pattern TAG_REGEX = Pattern.compile("<style>(.+?)</style>", Pattern.DOTALL);

	public static final String PREFIX = "stream2file";
	public static final String SUFFIX = ".tmp";

	public static File stream2file(InputStream in) throws IOException {
		final File tempFile = File.createTempFile(PREFIX, SUFFIX);
		tempFile.deleteOnExit();
		try (FileOutputStream out = new FileOutputStream(tempFile)) {
			IOUtils.copy(in, out);
		}
		return tempFile;
	}

}
