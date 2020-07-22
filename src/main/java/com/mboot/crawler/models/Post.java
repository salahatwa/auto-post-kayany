package com.mboot.crawler.models;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class Post {
	private long id;

	private String summary;

	private String editor;

	private String thumbnail;

	private int featured;

	private int comments;

	private Date created;

	private int favors;

	private int weight;

	private String title;

	private long authorId;

	private String content;

	private String tags;

	private String attribute;

	private List<String> tagsArray;

	private int channelId;

	private int views;

	private int status;

}
