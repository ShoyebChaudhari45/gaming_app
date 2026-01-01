package com.example.gameapp.models;

public class GameModel {

    private String name;
    private String result;
    private String time;

    // Required empty constructor (important for Firebase / Gson)
    public GameModel() {
    }

    public GameModel(String name, String result, String time) {
        this.name = name;
        this.result = result;
        this.time = time;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getResult() {
        return result;
    }

    public String getTime() {
        return time;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
