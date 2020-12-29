package de.bluewolf.wolfbot.utils;

import java.util.Date;
import java.util.HashMap;

import org.joda.time.Interval;
import org.joda.time.Period;

/**
 * @author Bluewolf787
 * @project wolfbot
 * @package de.bluewolf.wolfbot.utils
 * @created 26/Dez/2020 - 13:53
 */
public class Timer
{

    private static final HashMap<String, Date> times = new HashMap<>();

    public static void startTimer()
    {
        Date startTime = new Date();
        times.put("start", startTime);
    }

    public static String getElapsedTime()
    {
        String elapsedTime = "";

        Date startTime = times.get("start");
        Date currentTime = new Date();

        Interval interval = new Interval(startTime.getTime(), currentTime.getTime());
        Period period = interval.toPeriod();

        elapsedTime = String.format("%d Days %d Hours %d Minutes %d Seconds%n", period.getDays(), period.getHours(), period.getMinutes(), period.getSeconds());

        return elapsedTime;
    }

}
