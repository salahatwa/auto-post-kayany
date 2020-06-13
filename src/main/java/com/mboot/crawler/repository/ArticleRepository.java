package com.mboot.crawler.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mboot.crawler.entity.Article;

@Repository
public interface ArticleRepository extends CrudRepository<Article, Long> {

	Optional<Article> findByTitle(String title);
}
