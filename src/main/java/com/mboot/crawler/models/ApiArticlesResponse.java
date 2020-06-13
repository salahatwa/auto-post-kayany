package com.mboot.crawler.models;

import java.util.ArrayList;

/**
 * Represents attributes that are common to the NewsAPI REST endpoints that
 * respond with news articles.
 */
public class ApiArticlesResponse extends ApiResponse {
	private Integer totalResults;
	private ArrayList<ArticleNews> articles;

	public ApiArticlesResponse() {
		super();
		this.setTotalResults(0);
	}

	void setTotalResults(Integer totalResults) {
		this.totalResults = totalResults;
	}

	void setArticles(ArrayList<ArticleNews> articles) {
		this.articles = articles;
	}

	/**
	 * @return Total number of news articles in the response. For the
	 *         EverythingEndpoint, there may be more articles that match the given
	 *         query than are actually returned in the response.
	 */
	public Integer totalResults() {
		return this.totalResults;
	}

	/**
	 * @return ArrayList of news Articles, where each element of the ArrayList is an
	 *         Article.
	 */
	public ArrayList<ArticleNews> articles() {
		return this.articles;
	}
}
