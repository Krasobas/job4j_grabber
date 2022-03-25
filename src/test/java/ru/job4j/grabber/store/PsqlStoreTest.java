package ru.job4j.grabber.store;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.job4j.grabber.model.Post;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class PsqlStoreTest {
    static Connection connection;

    @BeforeClass
    public static void initConnection() {
        try (InputStream in = PsqlStoreTest.class.getClassLoader().getResourceAsStream("test.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")

            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @AfterClass
    public static void closeConnection() throws SQLException {
        connection.close();
    }

    @After
    public void wipeTable() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("delete from post")) {
            statement.execute();
        }
    }

    @Test
    public void whenSavePostAndFindByGeneratedIdThenMustBeTheSame() {
        PsqlStore app =  new PsqlStore(connection);
        Post post = new Post("title1", "link1", "description1", LocalDateTime.now());
        app.save(post);
        assertEquals(app.findById(post.getId()), post);
    }

    @Test
    public void whenSaveSomePostsAndGetAllListMustBeSame() {
        PsqlStore app =  new PsqlStore(connection);
        Post post1 = new Post("title1", "link1", "description1", LocalDateTime.now());
        Post post2 = new Post("title2", "link2", "description2", LocalDateTime.now());
        app.save(post1);
        app.save(post2);
        assertEquals(app.getAll(), List.of(post1, post2));
    }
}