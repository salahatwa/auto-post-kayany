package com.mboot.crawler.models;

import lombok.Data;

@Data
public class ArticleNews {

	private int id;
	private Source source;
	private String title;
	private String description;
	private String url;
	private String urlToImage;

	@Data
	public class Source {
//		private int id;
		private String name;
	}

}
