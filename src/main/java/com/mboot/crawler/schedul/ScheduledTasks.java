package com.mboot.crawler.schedul;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.mboot.crawler.entity.Article;
import com.mboot.crawler.models.ApiArticlesResponse;
import com.mboot.crawler.models.ArticleNews;
import com.mboot.crawler.repository.ArticleRepository;
import com.mboot.crawler.service.ArticleTemplateService;

/**
 * Created by ssatwa on 22-07-2020.
 */
@Component
public class ScheduledTasks {

	private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	@Autowired
	private ArticleRepository articleRepo;
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ArticleTemplateService articleTemplateService;

	@Scheduled(cron = "0 0/15 * * * ?")
	public void scheduleTaskWithCronExpression() {
		logger.info("Started Cron Task :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
		ResponseEntity<ApiArticlesResponse> responseEntityStr = restTemplate.getForEntity(
				"https://newsapi.org/v2/top-headlines?country=eg&apiKey=52945e5308824c24ad14ab8d43e072b1",
				ApiArticlesResponse.class);

		ApiArticlesResponse results = responseEntityStr.getBody();
		List<ArticleNews> articles = results.articles();
		for (ArticleNews article : articles) {
			if (Objects.nonNull(article.getTitle()) && !articleRepo.findByTitle(article.getTitle()).isPresent()) {
				Article art = articleRepo.save(new Article(article.getTitle()));

				try {
					article.setId(art.getId());
					String imgURL = articleTemplateService.buildTemplateImage(article);

//					articleTemplateService.schedule(article, imgURL);
					articleTemplateService.createKayanyPost(article, imgURL);
					
					Thread.sleep(60000);
				} catch (Exception e) {

				}

			}
		}

		logger.info("Schedule Task Finished");

		System.out.println(responseEntityStr.getBody());
		System.gc();

	}

}
