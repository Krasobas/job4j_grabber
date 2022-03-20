package ru.job4j.grabber.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class HabrCareerDateTimeParser implements DateTimeParser {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ssX");
    @Override
    public LocalDateTime parse(String parse) throws ParseException {
        return DATE_FORMAT.parse(parse)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
