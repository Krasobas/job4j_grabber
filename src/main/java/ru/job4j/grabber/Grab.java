package ru.job4j.grabber;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import ru.job4j.grabber.parse.Parse;
import ru.job4j.grabber.store.Store;

public interface Grab {
    void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException;
}
