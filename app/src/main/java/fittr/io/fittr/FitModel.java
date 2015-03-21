package fittr.io.fittr;

import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.data.DataType;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Gets data from Google Fit
 *
 * Created by creston on 3/21/15.
 */
public class FitModel {

    public FitModel() {

    }

    public int getCaloriesExpendedAtDate(Calendar date) {
        Calendar startCal = Calendar.getInstance();
        startCal.set(
                date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH),
                0,0,0
        );
        Calendar endCal = Calendar.getInstance();
        endCal.set(
                date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH),
                24,0,0
        );

        long startTime = startCal.getTimeInMillis();
        long endTime = endCal.getTimeInMillis();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .read(DataType.AGGREGATE_CALORIES_EXPENDED)
                .build();

    }

}
