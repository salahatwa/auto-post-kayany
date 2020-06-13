package com.mboot.crawler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FreemarkerConfig {

	@Bean(name = "freemarkerConfiguration")
	public freemarker.template.Configuration getFreeMarkerConfiguration() {
		freemarker.template.Configuration config = new freemarker.template.Configuration(
				freemarker.template.Configuration.getVersion());
		config.setClassForTemplateLoading(this.getClass(), "/templates/");
		return config;
	}
}
