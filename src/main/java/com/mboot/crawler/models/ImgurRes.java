package com.mboot.crawler.models;

@lombok.Data
public class ImgurRes {
	private Data data;

	private String success;

	private String status;

	public ImgurRes() {

	}

	@lombok.Data
	public static class Data {

		private String link;

		public Data() {
		}

	}
}
