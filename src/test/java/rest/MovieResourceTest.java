package rest;

import entities.Movie;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//@Disabled
public class MovieResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    
    private Movie m1;
    private Movie m2;
    private Movie m3;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //First Drop and Rebuild the test database 
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        //Set System property so the project executed by the Grizly-server wil use this same database
        EMF_Creator.startREST_TestWithDB();
        
        httpServer = startServer();

        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;

        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer()  {
       
       // System.in.read();
       
        httpServer.shutdownNow();
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        m1 = new Movie(2001, "Harry Potter and the Philosopher's Stone", new String[]{"Daniel Radcliffe", "Emma Watson", "Alan Rickman", "Rupert Grint"});
        m2 = new Movie(2002, "Harry Potter and the Chamber of Secrets", new String[]{"Daniel Radcliffe", "Emma Watson", "Alan Rickman", "Rupert Grint"});
        m3 = new Movie(2019, "Once Upon a Time... in Hollywood", new String[]{"Leonardo DiCaprio", "Brad Pitt", "Margot Robbie"});
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE from Movie").executeUpdate();
            em.persist(m1);
            em.persist(m2);
            em.persist(m3);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void serverIsRunning() {
        System.out.println("Testing is server UP");
        //Gherkin Syntax
        given().when().get("/movie").then().statusCode(200);
        //Hamcrest matcher
        given().when().get("/movie").then().assertThat().statusCode(200);
    }
    @Test
    public void contentType() {
        //Gherkin Syntax
        given().when().get("/movie").then().contentType(ContentType.JSON);
        //Hamcrest matcher
        given().when().get("/movie").then().assertThat().contentType(ContentType.JSON);
    }
    
    @Test
    public void demonStrateLogging() {
        
        given().log().all().when().get("/movie").then().log().body();
       
    }

    @Test
    public void testMovieCount() {
        given().
                get("/movie/count")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("count", equalTo(3));
    }

    @Test
    public void testGetAll() {
        //TODO
        //Create a test for the endpoint api/movie/all and assert that the response includes the right amount of movies 
        //(what you have setup for the test) and preferably also that you got the expected movies.
        given().
                get("/movie/all")
                .then()
                .assertThat()
                .body("title", hasItems("Harry Potter and the Philosopher's Stone",
                        "Harry Potter and the Chamber of Secrets",
                        "Once Upon a Time... in Hollywood"))
                .assertThat()
                .body("title", hasSize(3));
    }

    
    @Test
    public void testFindByTitle() {
        //TODO
        //Create a test for an endpoint: api/movie/title/{title}. Use a name you know exists, and (for red students) 
        //also try with a name that does not exist (obviously this requires that you know what you return in such a case)
        given().
                pathParam("title", "Harry Potter")
                .get("/movie/title/{title}")
                .then()
                .assertThat()
                .body("title", hasItems("Harry Potter and the Philosopher's Stone",
                        "Harry Potter and the Chamber of Secrets"));
    }
    
    @Test
    public void testFindByTitleNotFound() {
       //TODO, if you have time
    }
    
     @Test
    public void testFindById() {
        //given().get("/movie/{id}", m2.getId())
        //TODO
        //Create a test for an endpoint:  api/movie/{id} and verify that you get the expected Movie
//        given().
//                pathParam("id", 1)
//                .get("/movie/{id}")
//                .then()
//                .assertThat()
//                .body("title", equalTo("Harry Potter and the Chamber of Secrets"));
        given().
                get("/movie/" + m2.getId())
                .then()
                .assertThat()
                .body("id", equalTo(m2.getId()))/*.log().body()*/;
    }
}


 