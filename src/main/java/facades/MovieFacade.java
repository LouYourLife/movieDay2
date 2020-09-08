package facades;

import dtos.MovieDTO;
import entities.Movie;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import utils.EMF_Creator;



public class MovieFacade  {

    private static MovieFacade instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private MovieFacade() {}

    /**
     *
     * @param _emf
     * @return an instance of this facade class.
     */
    public static MovieFacade getMovieFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new MovieFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public long getMovieCount() {
        EntityManager em = emf.createEntityManager();
        try {
            long movieCount = (long) em.createQuery("SELECT COUNT(m) FROM Movie m").getSingleResult();
            return movieCount;
        } finally {
            em.close();
        }
    }

    public List<MovieDTO> getAllMovies() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Movie> query =  em.createQuery("SELECT m FROM Movie m",Movie.class);
        List<Movie> movies = query.getResultList();
        List<MovieDTO> movieDTOs = new ArrayList();
        movies.forEach((Movie movie) -> {
            movieDTOs.add(new MovieDTO(movie));
        });
        return movieDTOs;     
    }

    public List<MovieDTO> getMoviesByTitle(String title) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m WHERE m.title LIKE :title", Movie.class);
        query.setParameter("title", "%"+title+"%");
        List<Movie> movies = query.getResultList();
        List<MovieDTO> movieDTOs = new ArrayList();
        movies.forEach((Movie movie) -> {
            movieDTOs.add(new MovieDTO(movie));
        });
        return movieDTOs;
    }


    public MovieDTO getMovieById(int id) {
        EntityManager em = emf.createEntityManager();
        Movie m=  em.find(Movie.class, id);
        return new MovieDTO(m);
    }

    public MovieDTO createMovie(int year, String name, String[] actors) {
        Movie movie = new Movie(year,name,actors);
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            em.persist(movie);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new MovieDTO(movie);
    }

    
    public static void main(String[] args) {
        //Create emf pointing to the dev-database
        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
        
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE from Movie").executeUpdate();
            em.persist(new Movie(2002, "Harry Potter and the Chamber of Secrets", new String[]{"Daniel Radcliffe", "Emma Watson", "Alan Rickman", "Rupert Grint"}));
            em.persist(new Movie(2001, "Harry Potter and the Philosopher's Stone", new String[]{"Daniel Radcliffe", "Emma Watson","Alan Rickman", "Rupert Grint"}));
            em.persist(new Movie(2019, "Once Upon a Time... in Hollywood", new String[]{"Leonardo DiCaprio", "Brad Pitt", "Margot Robbie"}));
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
