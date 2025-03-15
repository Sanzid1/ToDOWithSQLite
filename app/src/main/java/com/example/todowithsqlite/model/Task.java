package com.example.todowithsqlite.model;

public class Task {
    private long id;
    private String title;
    private String description;
    private String date;
    private boolean completed;

    // Constructor with id (for existing tasks)
    public Task(long id, String title, String description, String date, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.completed = completed;
    }

    // Constructor without id (for new tasks)
    public Task(String title, String description, String date, boolean completed) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.completed = completed;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", completed=" + completed +
                '}';
    }
}