package fittr.io.fittr;

import java.util.Calendar;

/**
 * Created by creston on 3/21/15.
 */
public class Util {

    public static long dayStartMillis() {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH),
                0,0,0);
        return cal.getTimeInMillis();
    }

    public static long dayEndmillis() {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH),
                0,0,0);
        cal.add(Calendar.DATE, 1);
        return cal.getTimeInMillis();
    }

    public static Calendar now() {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        return now;
    }
}
