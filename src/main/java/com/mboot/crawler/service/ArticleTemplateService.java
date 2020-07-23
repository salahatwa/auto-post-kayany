package com.mboot.crawler.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.mail.MessagingException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.XmlSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mboot.crawler.PostDto;
import com.mboot.crawler.models.ArticleNews;
import com.mboot.crawler.models.ImgurRes;
import com.mboot.crawler.models.Post;
import com.openhtmltopdf.bidi.support.ICUBidiReorderer;
import com.openhtmltopdf.bidi.support.ICUBidiSplitter;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.TextDirection;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

//import com.openhtmltopdf.java2d.Java2DRenderer;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class ArticleTemplateService {

	@Autowired
	private Configuration freemarkerConfiguration;

	@Autowired
	private PDFParserService pdfService;

	ObjectMapper mapper = new ObjectMapper();

	PdfRendererBuilder builder = new PdfRendererBuilder();
	HtmlCleaner cleaner = new HtmlCleaner();
	XmlSerializer xmlSerializer;

	@PostConstruct
	private void init() throws IOException {

		System.setProperty("wagon.http.ssl.insecure", "true");
		System.setProperty("wagon.http.ssl.allowall", "true");
		System.setProperty("wagon.http.ssl.ignore.validity.dates", "true");
//		System.setProperty("","");
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

	public String buildTemplateHtml(ArticleNews article) throws MessagingException, IOException, TemplateException {

		Map<String, Object> model = mapper.convertValue(article, new TypeReference<Map<String, Object>>() {
		});

		Template t = freemarkerConfiguration.getTemplate("post-template-4.ftl");
		String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

//		TagNode rootTagNode = cleaner.clean(html);
//
//		html = xmlSerializer.getAsString(rootTagNode, "ISO-8859-1");

		return html;
	}

	public String buildTemplateImage(ArticleNews article) throws MessagingException, IOException, TemplateException {

//		article.setUrlToImage(imageToBase64(article.getUrlToImage()));

		Map<String, Object> model = mapper.convertValue(article, new TypeReference<Map<String, Object>>() {
		});

		Template t = freemarkerConfiguration.getTemplate("post-template-5.ftl");
		String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {

			ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
			pdfService.parseTemplate(html, pdfOutputStream);

			PDDocument doc = PDDocument.load(pdfOutputStream.toByteArray());
			PDFRenderer pdfRenderer = new PDFRenderer(doc);
			BufferedImage bffim = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);

			ImageIO.write(bffim, "png", baos);

			doc.close();

			String link = getImageLink(baos.toByteArray());
			System.err.println(link);
			return link;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			baos.close();
			System.gc();
		}
		return html;

	}

	private String imageToBase64(String urlToImage) {
		byte[] imageBytes = restTemplate.getForObject(urlToImage, byte[].class);
		System.out.println(urlToImage + "-------------" + imageBytes.length);
//		for (byte b : imageBytes) {
//			System.out.println(b);
//		}
		return "data:image/jpeg;charset=utf-8;" + Base64.getEncoder().encodeToString(imageBytes);
	}

	public static Resource getUserFileResource(byte[] bytes) throws IOException {

		return new ByteArrayResource(bytes);
	}

	private String getImageLink(byte[] bytes) throws IOException {
		final String uri = "https://api.imgur.com/3/image";

		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add("image", getUserFileResource(bytes));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.add("Authorization", "Client-ID c900a9acad45b7f");
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<ImgurRes> response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, ImgurRes.class);
		System.out.println("response status: " + response.getStatusCode());
		System.out.println("response body: " + response.getBody().getData().getLink());

		return response.getBody().getData().getLink();
	}

	@Autowired
	private RestTemplate restTemplate;

	public void createKayanyPost(ArticleNews articleNews, String url) {
		Post post = new Post();
		post.setAuthorId(1);
		post.setChannelId(4);
		post.setCreated(new Date());

		if (articleNews.getTitle().length() > 64)
			post.setTitle(articleNews.getTitle().substring(0, 60) + "..");
		else
			post.setTitle(articleNews.getTitle());

		if (articleNews.getDescription().length() > 140)
			post.setSummary(articleNews.getDescription().substring(0, 135) + "..");
		else
			post.setSummary(articleNews.getDescription());

		try {
			post.setContent(buildTemplateHtml(articleNews));
		} catch (MessagingException | IOException | TemplateException e) {
			e.printStackTrace();
		}

		post.setTags("أخبار");
		post.setEditor("tinymce");
		try {
			System.err.println(mapper.writeValueAsString(post));
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(post), headers);
			ResponseEntity<PostResult> responseEntityStr = restTemplate
					.postForEntity("https://kayany.herokuapp.com/post/create", request, PostResult.class);
			System.out.println("Done Post:[" + responseEntityStr.getStatusCodeValue() + "]:"
					+ "https://kayany.herokuapp.com/post/" + responseEntityStr.getBody().getPostId());

			Thread.sleep(60000);
			System.gc();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PostResult {
		private long postId;
	}

	public void schedule(ArticleNews articleNews, String url) throws java.lang.Exception {

		final long ONE_MINUTE_IN_MILLIS = 60000;// millisecs

		Calendar cal = Calendar.getInstance();
		long t = cal.getTimeInMillis();
		Date date = new Date(t + (2 * ONE_MINUTE_IN_MILLIS));

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat tf = new SimpleDateFormat("HH:mm");

		// Use Madrid's time zone to format the date in
		df.setTimeZone(TimeZone.getTimeZone("Asia/Riyadh"));
		tf.setTimeZone(TimeZone.getTimeZone("Asia/Riyadh"));

//		cal.add(Calendar.DATE, 1);
//		date = cal.getTime();

		System.out.println("Date in Riyadh: " + df.format(date));
		System.out.println("Time in Riyadh: " + tf.format(date));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		PostDto post = new PostDto();

		String content = articleNews.getTitle() + "\n";
		content += articleNews.getSource().getName() + "\n";
		content += articleNews.getUrl() + "\n";
		post.setContent(content);
		post.setImg(url);
		post.setFbId("575803389234632");
		post.setTime(tf.format(date));
		post.setDate(df.format(date));
		post.setEnabled(true);

		System.out.println(">>>>>>>>>:" + post);
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(">>>>>>>>>:" + mapper.writeValueAsString(post));

		try {
			HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(post), headers);
			ResponseEntity<String> responseEntityStr = restTemplate.postForEntity(
					"https://auto-y.herokuapp.com/internal/112650030474835/new?timezone=-180", request, String.class);
			System.out.println("Done schedule:" + responseEntityStr);

			Thread.sleep(60000);
			System.gc();
		} catch (Exception ex) {
		}

	}

}
