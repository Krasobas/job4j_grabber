package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    private Map<String, String> properties;

    public AlertRabbit() {
        readProperties();
    }

    private List<String> validate() {
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(Path.of("./src/main/resources/rabbit.properties"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String line : lines) {
            if (!Pattern.matches("rabbit\\..+=.+", line)) {
                throw new IllegalArgumentException("Check your properties file. Only key=value pattern is admissible.");
            }
        }
        return lines;
    }

    private void readProperties() {
        properties = validate().stream()
                .map(x -> x.split("="))
                .filter(x -> x.length == 2)
                .collect(Collectors.toMap(x -> x[0], x -> x[1]));
    }

    public int getProperty(String key) {
        return Integer.parseInt(properties.get(key));
    }

    public static void main(String[] args) {
        AlertRabbit app = new AlertRabbit();
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class).build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(app.getProperty("rabbit.interval"))
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
        }
    }
}