package ru.job4j.grabber;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class PsqlStore implements Store, AutoCloseable {
    private static final String TABLE = "post";
    private Connection cn;
    private boolean tableExists;

    public PsqlStore(Properties cfg) {
        init(cfg);
        checkTable();
    }

    private void init(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("driver-class-name"));
            cn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("username"),
                    cfg.getProperty("password")
            );
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkTable() {
        try (Statement st = cn.createStatement()) {
            String sql = readScript(Path.of("./db/scripts/checkTable.sql"));
            try (ResultSet rs = st.executeQuery(sql)) {
                if (rs.next()) {
                    tableExists = rs.getBoolean(1);
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void createTable() {
        try (Statement st = cn.createStatement()) {
            String sql = readScript(Path.of("./db/scripts/createTable.sql"));
            tableExists = st.executeUpdate(sql) == 0;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private Post createPost(ResultSet rs) throws SQLException {
        return new Post(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("link"),
                rs.getString("text"),
                rs.getTimestamp("created").toLocalDateTime());
    }

    private String readScript(Path path) throws IOException {
        return Files.newBufferedReader(path)
                .lines()
                .collect(Collectors.joining());
    }

    @Override
    public void save(Post post) {
        if (!tableExists) {
            createTable();
        }
        String sql = String.format("insert into %s(name, text, link, created) "
                + "values(?, ?, ?, ?) on conflict do nothing;", TABLE);
        try (PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setString(1, post.getTitle());
            pst.setString(2, post.getDescription());
            pst.setString(3, post.getLink());
            pst.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            pst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        if (!tableExists) {
            return Collections.emptyList();
        }
        List<Post> rsl = new ArrayList<>();
        try (Statement st = cn.createStatement()) {
            String sql = String.format("select * from %s;", TABLE);
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                rsl.add(createPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public Post findById(int id) {
        if (!tableExists) {
            return null;
        }
        Post rsl = null;
        try (Statement st = cn.createStatement()) {
            String sql = String.format("select * from %s where id = %d;", TABLE, id);
            try (ResultSet rs = st.executeQuery(sql)) {
                if (rs.next()) {
                    rsl = createPost(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public void readStore() {
        String sql = String.format("select * from %s;", TABLE);
        try (Statement st = cn.createStatement()) {
            try (ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    System.out.printf("id = %d, title = %s,%n%n%s%n%ncreated_date = %tT%n%s%n",
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("text"),
                            rs.getTimestamp("created").toLocalDateTime(),
                            "=".repeat(50));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        if (cn != null) {
            cn.close();
        }

    }

    public static void main(String[] args) {
        try (InputStream in = PsqlStore.class.getClassLoader()
                .getResourceAsStream("rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            try (PsqlStore app =  new PsqlStore(config)) {
                app.save(new Post("title", "link4", "description", LocalDateTime.now()));
                app.getAll().forEach(System.out::println);
                System.out.println(app.findById(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
