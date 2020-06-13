package com.mboot.crawler;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PostDto {

	private String content;
	private boolean enabled = true;
	private String date;
	private String time;
	private String img;
	private String latitude;
	private String longitude;
	private boolean posted = false;
	private String fbId;

}
