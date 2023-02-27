package com.ahmed.newsapp;

public class NewsModel {
    private String title;
    private String type;
    private String sectionName ;
    private String date;
    private String url;
    private String imgUrl;
    private String author;

    public NewsModel(String title, String type, String sectionName, String date, String url  , String author) {
        this.title = title;
        this.type = type;
        this.sectionName = sectionName;
        this.date = date;
        this.url = url;
        this.author = author;
    }


    public String getTitle() {
        return title;
    }
    public String getType() { return type; }
    public String getSectionName() { return sectionName; }
    public String getDate() { return date; }
    public String getUrl() { return url; }
    public String getImg_url() { return imgUrl; }
    public String getAuthorName() { return author; }
}
