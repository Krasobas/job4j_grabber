package ru.job4j.grabber.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class HabrCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ssX");
        return sdf.parse(parse)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
