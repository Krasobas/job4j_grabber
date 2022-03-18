package ru.job4j.grabber;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class PsqlStore implements Store, AutoCloseable {
    private Connection cn;
    private boolean tableExists;

    public PsqlStore() {
        init();
        checkTable();
        System.out.println(tableExists);
    }

    private void init() {
        try (InputStream in = PsqlStore.class.getClassLoader()
                .getResourceAsStream("rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            cn = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (IOException | ClassNotFoundException | SQLException e) {
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
    }

    @Override
    public List<Post> getAll() {
        if (!tableExists) {
            return Collections.emptyList();
        }
        return null;
    }

    @Override
    public Post findById(int id) {
        if (!tableExists) {
            return null;
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        if (cn != null) {
            cn.close();
        }

    }

    public static void main(String[] args) {
        new PsqlStore();
    }
}
