package ru.job4j.grabber;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private static final String TABLE = "post";
    private Connection cn;
    private boolean tableExists;

    public PsqlStore() {
        init();
        checkTable();
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
            String sql = String.format("select exists (select 1 from information_schema.columns"
                    + "where table_name = '%s'", TABLE);
            try (ResultSet rs = st.executeQuery(sql)) {
                if (rs.next()) {
                    tableExists = rs.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable() {
        try (Statement st = cn.createStatement()) {
            String sql = String.format("create table if not exists %s (%s, %s, %s, %s, %s);",
                    TABLE,
                    "id serial primary key",
                    "name varchar(255)",
                    "text text",
                    "link varchar(255) unique",
                    "created timestamp");
            tableExists = st.executeUpdate(sql) == 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
}
