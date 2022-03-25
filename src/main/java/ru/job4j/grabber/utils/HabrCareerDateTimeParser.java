package ru.job4j.grabber.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HabrCareerDateTimeParser implements DateTimeParser {
    private static final Logger LOG = LoggerFactory.getLogger(HabrCareerDateTimeParser.class.getName());
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ssX");
    @Override
    public LocalDateTime parse(String parse) {
        try {
            return DATE_FORMAT.parse(parse)
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (ParseException e) {
            LOG.error("Impossible to parse String to Date.", e);
        }
        return null;
    }
}
