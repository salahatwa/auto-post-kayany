package com.mboot.crawler.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "article")
public class Article {

	@Id
	@GeneratedValue
	private int id;

	@Column(length = 10485760, nullable = false)
	private String title;

	public Article(String title) {
		this.title = title;
	}

}
