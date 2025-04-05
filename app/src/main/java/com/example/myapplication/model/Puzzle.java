package com.example.myapplication.model;

public class Puzzle {
    private String title;
    private String question;
    private String answer;
    private String mood;

    public Puzzle(String title, String question, String answer, String mood) {
        this.title = title;
        this.question = question;
        this.answer = answer;
        this.mood = mood;
    }

    public String getTitle() { return title; }
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public String getMood() { return mood; }
}
