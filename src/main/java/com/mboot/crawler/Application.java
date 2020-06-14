package com.mboot.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.mboot.crawler.repository.ArticleRepository;
import com.mboot.crawler.service.ArticleTemplateService;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableScheduling
public class Application implements ApplicationRunner {

    private static Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    private ArticleTemplateService emailService;
    
    @Autowired
    private ArticleRepository articleRepo;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
    	
//    	articleRepo.deleteAll();
//        log.info("Sending Email with Freemarker HTML Template Example");
//
//        Mail mail = new Mail();
//        mail.setFrom("no-reply@memorynotfound.com");
//        mail.setTo("oliviersips@hotmail.com");
//        mail.setSubject("Sending Email with Freemarker HTML Template Example");
//
//        Map<String, Object> model = new HashMap<String, Object>();
//        model.put("name", "Memorynotfound.com");
//        model.put("location", "Belgium");
//        model.put("signature", "http://memorynotfound.com");
//        mail.setModel(model);
//
//        emailService.sendSimpleMessage(mail);
    }
}
