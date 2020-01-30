package csell.com.vn.csell.constants;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import csell.com.vn.csell.R;
import csell.com.vn.csell.views.csell.activity.MainActivity;

/**
 * Created by cuong.nv on 4/25/2018.
 */

public class TimeAgo {

    //        public static final Map<String, Long> times = new LinkedHashMap<>();
    public static Map<String, Long> times;

//    static {
//        times.put(MainActivity.mainContext.getString(R.string.years), TimeUnit.DAYS.toMillis(730));
//        times.put(MainActivity.mainContext.getString(R.string.year), TimeUnit.DAYS.toMillis(365));
//        times.put(MainActivity.mainContext.getString(R.string.months), TimeUnit.DAYS.toMillis(60));
//        times.put(MainActivity.mainContext.getString(R.string.month), TimeUnit.DAYS.toMillis(30));
//        times.put(MainActivity.mainContext.getString(R.string.weeks), TimeUnit.DAYS.toMillis(14));
//        times.put(MainActivity.mainContext.getString(R.string.week), TimeUnit.DAYS.toMillis(7));
//        times.put(MainActivity.mainContext.getString(R.string.days), TimeUnit.DAYS.toMillis(2));
//        times.put(MainActivity.mainContext.getString(R.string.day_), TimeUnit.DAYS.toMillis(1));
//        times.put(MainActivity.mainContext.getString(R.string.hours), TimeUnit.HOURS.toMillis(2));
//        times.put(MainActivity.mainContext.getString(R.string.hour_), TimeUnit.HOURS.toMillis(1));
//        times.put(MainActivity.mainContext.getString(R.string.munites), TimeUnit.MINUTES.toMillis(2));
//        times.put(MainActivity.mainContext.getString(R.string.minite_), TimeUnit.MINUTES.toMillis(1));
//        times.put(MainActivity.mainContext.getString(R.string.just_now), TimeUnit.SECONDS.toMillis(60));
//    }

    public static String toRelative(long duration, int maxLevel) {
        times = new LinkedHashMap<>();
        times.put(MainActivity.mainContext.getString(R.string.years), TimeUnit.DAYS.toMillis(730));
        times.put(MainActivity.mainContext.getString(R.string.year), TimeUnit.DAYS.toMillis(365));
        times.put(MainActivity.mainContext.getString(R.string.months), TimeUnit.DAYS.toMillis(60));
        times.put(MainActivity.mainContext.getString(R.string.month), TimeUnit.DAYS.toMillis(30));
        times.put(MainActivity.mainContext.getString(R.string.weeks), TimeUnit.DAYS.toMillis(14));
        times.put(MainActivity.mainContext.getString(R.string.week), TimeUnit.DAYS.toMillis(7));
        times.put(MainActivity.mainContext.getString(R.string.days), TimeUnit.DAYS.toMillis(2));
        times.put(MainActivity.mainContext.getString(R.string.day_), TimeUnit.DAYS.toMillis(1));
        times.put(MainActivity.mainContext.getString(R.string.hours), TimeUnit.HOURS.toMillis(2));
        times.put(MainActivity.mainContext.getString(R.string.hour_), TimeUnit.HOURS.toMillis(1));
        times.put(MainActivity.mainContext.getString(R.string.munites), TimeUnit.MINUTES.toMillis(2));
        times.put(MainActivity.mainContext.getString(R.string.minite_), TimeUnit.MINUTES.toMillis(1));
        times.put(MainActivity.mainContext.getString(R.string.just_now), TimeUnit.SECONDS.toMillis(60));

        StringBuilder res = new StringBuilder();
        int level = 0;
        for (Map.Entry<String, Long> time : times.entrySet()) {
            double timeDelta1 = (double) duration / time.getValue();
            long timeDelta2 = duration / time.getValue();
            long timeDelta3 = (long) Math.ceil(timeDelta1);
            if (timeDelta2 > 0) {
                if (time.getValue() == TimeUnit.DAYS.toMillis(730) ||
                        time.getValue() == TimeUnit.DAYS.toMillis(60) ||
                        time.getValue() == TimeUnit.DAYS.toMillis(14) ||
                        time.getValue() == TimeUnit.DAYS.toMillis(2) ||
                        time.getValue() == TimeUnit.HOURS.toMillis(2) ||
                        time.getValue() == TimeUnit.MINUTES.toMillis(2)) {
                    long timeTemp = timeDelta3 * 2;
                    res.append(timeTemp)
                            .append(" ")
                            .append(time.getKey())
                            .append(timeTemp > 1 ? "" : "")
                            .append(", ");
                    duration -= time.getValue() * timeTemp;
                } else {
                    res.append(timeDelta3)
                            .append(" ")
                            .append(time.getKey())
                            .append(timeDelta3 > 1 ? "" : "")
                            .append(", ");
                    duration -= time.getValue() * timeDelta3;
                }
                level++;
            }
            if (level == maxLevel) {
                break;
            }
        }
        if ("".equals(res.toString())) {
            return MainActivity.mainContext.getString(R.string.just_now);
        } else {
            res.setLength(res.length() - 2);
            res.append(MainActivity.mainContext.getString(R.string.ago_));
            return res.toString();
        }
    }

    public static String toRelative(long duration) {
        return toRelative(duration, times.size());
    }

    public static String toRelative(Date start, Date end) {
        assert start.after(end);
        return toRelative(end.getTime() - start.getTime());
    }

    public static String toRelative(Date start, Date end, int level) {
        assert start.after(end);
        return toRelative(end.getTime() - start.getTime(), level);
    }

    public static String toRelative(Long startMilitimes, Long endMilitimes, int level) {

        return toRelative(endMilitimes - startMilitimes, level);
    }
}
