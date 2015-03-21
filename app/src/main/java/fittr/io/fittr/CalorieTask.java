package fittr.io.fittr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.result.DataReadResult;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Gets data from Google Fit
 *
 * Created by creston on 3/21/15.
 */
public class CalorieTask extends AsyncTask<Void, Void, Integer> {

    private GoogleApiClient mClient;
    private long startTime;
    private long endTime;

    // TODO: more accurate conversion
    public static final int CALORIES_PER_STEP = 20;

    public CalorieTask(GoogleApiClient mClient, long startTime, long endTime) {
        this.mClient = mClient;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    protected Integer doInBackground(Void... input) {
        System.out.println("Getting Calorie data....");

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .build();

        DataReadResult readResult = Fitness.HistoryApi.readData(
                mClient, readRequest
        ).await(1, TimeUnit.MINUTES);

        int steps = 0;
        for (DataPoint dp : readResult.getBuckets().get(0).getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA).getDataPoints()) {
            for(Field field : dp.getDataType().getFields()) {
                if (field.getName().toLowerCase().equals("steps")) {
                    steps += dp.getValue(field).asInt();
                }
            }

        }

        int calories = steps / CALORIES_PER_STEP;

        System.out.println("Counted " + steps + " steps which burns " + calories + " calories.");

        return calories;

    }

}
