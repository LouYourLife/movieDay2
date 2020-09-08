package dtos;

import entities.Movie;

public class MovieDTO {
    private int id;
    private int year;
    private String title;
    private String[] actors;

    public MovieDTO(){}
    
    public MovieDTO(Movie movie) {
        this.id = movie.getId();
        this.year = movie.getYear();
        this.title = movie.getTitle();
        this.actors = movie.getActors();
    }
    public MovieDTO(int id, int year, String title, String[] actors) {
        this.id = id;
        this.year = year;
        this.title = title;
        this.actors = actors;
    }

    public int getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public String getTitle() {
        return title;
    }

    public String[] getActors() {
        return actors;
    }
    
   
    
}
