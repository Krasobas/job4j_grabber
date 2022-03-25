package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(AlertRabbit.class.getName());
    private Properties config;
    private static final String TABLE_NAME = "rabbit";
    private Connection cn;
    private boolean tableExists;

    public AlertRabbit() {
        init();
        checkTable();
        if (!tableExists) {
            createTable();
        }
    }

    private void init() {
        try (InputStream in = AlertRabbit.class.getClassLoader()
                .getResourceAsStream("rabbit.properties")) {
            config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            cn = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (IOException | ClassNotFoundException | SQLException e) {
            LOG.error("Impossible to create connection with database. Check your properties file.", e);
        }
    }

    private void checkTable() {
        try (Statement st = cn.createStatement()) {
            String sql = String.format("select exists (select 1 from information_schema.columns "
                            + "where table_name = '%s' and "
                            + "(column_name = '%s' or column_name = '%s'));",
                    TABLE_NAME,
                    "id",
                    "created_date");
            try (ResultSet rs = st.executeQuery(sql)) {
                if (rs.next()) {
                    tableExists = rs.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            LOG.error("Impossible to create SQL Statement or execute SQL query.", e);
        }
    }

    private void createTable() {
        try (Statement st = cn.createStatement()) {
            String sql = String.format("create table if not exists %s(%s, %s);",
                    TABLE_NAME,
                    "id serial primary key",
                    "created_date timestamp");
            tableExists = st.executeUpdate(sql) == 0;
        } catch (SQLException e) {
            LOG.error("Impossible to create SQL Statement or execute SQL query.", e);
        }
    }

    public Connection getConnection() {
        return cn;
    }

    public int getIntProperty(String key) {
        return Integer.parseInt(config.getProperty(key));
    }

    public void readStore() {
        String sql = String.format("select * from %s;", TABLE_NAME);
        try (Statement st = cn.createStatement()) {
            try (ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    System.out.printf("id = %d, created_date = %tT%n",
                            rs.getInt("id"),
                            rs.getTimestamp("created_date").toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            LOG.error("Impossible to create SQL Statement or execute SQL query.", e);
        }
    }

    @Override
    public void close() throws Exception {
        if (cn != null) {
            cn.close();
        }
    }

    public static void main(String[] args) {
        try (AlertRabbit app = new AlertRabbit()) {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", app.getConnection());
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(app.getIntProperty("rabbit.interval"))
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
            app.readStore();
        } catch (Exception e) {
            LOG.error("Check your main method.", e);
        }
    }

    public static class Rabbit implements Job {
        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
            Connection cn = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            String sql = String.format("insert into %s(created_date) values (?);", TABLE_NAME);
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                ps.execute();
            } catch (SQLException e) {
                LOG.error("Impossible to create SQL Statement or execute SQL query.", e);
            }
        }
    }
}