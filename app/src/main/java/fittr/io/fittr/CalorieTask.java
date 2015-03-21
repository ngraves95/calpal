package fittr.io.fittr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAPod;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;
import com.wolfram.alpha.WASubpod;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gets data from Google Fit
 *
 * Created by creston on 3/21/15.
 */
public class CalorieTask extends AsyncTask<Void, Void, Integer> {

    private GoogleApiClient mClient;
    private long startTime;
    private long endTime;

    public static final Pattern CALORIES_PATTERN = Pattern.compile("([0-9]+) Cal");


    private TextView destination;

    public CalorieTask(GoogleApiClient mClient, long startTime, long endTime, TextView destination) {
        this.mClient = mClient;
        this.startTime = startTime;
        this.endTime = endTime;
        this.destination = destination;
    }

    protected Integer doInBackground(Void... input) {
        System.out.println("Getting Calorie data....");

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_SPEED, DataType.AGGREGATE_SPEED_SUMMARY)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .setLimit(1)
                .build();

        DataReadResult readResult = Fitness.HistoryApi.readData(
                mClient, readRequest
        ).await(1, TimeUnit.MINUTES);

        DataReadRequest weightReadRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_WEIGHT)
                .setLimit(1)
                .setTimeRange(1, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult weightReadResult = Fitness.HistoryApi.readData(
                mClient, weightReadRequest
        ).await(1, TimeUnit.MINUTES);

        int steps = 0;
        float weight = 0; // kg

        for (DataPoint dp : readResult.getBuckets().get(0).getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA).getDataPoints()) {
            for(Field field : dp.getDataType().getFields()) {
                if (field.getName().toLowerCase().equals("steps")) {
                    steps += dp.getValue(field).asInt();
                }
            }
        }
        // TODO: get accurate speed data
        DataPoint wp = weightReadResult.getDataSet(DataType.TYPE_WEIGHT).getDataPoints().get(0);
        weight = wp.getValue(wp.getDataType().getFields().get(0)).asFloat();

        int calories = calcCalories(weight, steps);

        System.out.println("Counted " + steps + " steps which burns " + calories + " calories.");
        System.out.println("Average weight " + weight);

        return calories;

    }

    @Override
    protected void onPostExecute(Integer calsBurned) {
        int currentCalCount = Integer.parseInt(destination.getText().toString());
        currentCalCount -= calsBurned;
        destination.setText(currentCalCount + "");
    }

    private int calcCalories(float weight, int steps) {

        // create and perform WA queries with this object
        WAEngine engine = new WAEngine();

        engine.setAppID(MainActivity.WA_APP_ID);
        engine.addFormat("plaintext");

        // make query
        WAQuery query = engine.createQuery();
        query.setInput("calories burned from a " + weight + "kg male walking " + steps + "steps");

        // defaults
        int calories = 0;

        try {
            System.out.println("Query URL:");
            System.out.println(engine.toURL(query));
            System.out.println();

            WAQueryResult queryResult = engine.performQuery(query);
            if (queryResult.isError()) {
                System.out.println("Query error:");
                System.out.println("   error code: " + queryResult.getErrorCode());
                System.out.println("   error message: " + queryResult.getErrorMessage());
            } else if (!queryResult.isSuccess()) {
                System.out.println("Query was not understood; no results available.");
            } else {
                // Got a result
                System.out.println("Successful query. Pods follow:\n");
                for (WAPod pod : queryResult.getPods()) {
                    if (!pod.isError()) {
                        System.out.println(pod.getTitle());
                        // find pod with input interpretation
                        if (pod.getTitle().toLowerCase().contains("metabolic properties")) {
                            for (WASubpod subpod : pod.getSubpods()) {
                                for (Object element : subpod.getContents()) {
                                    if (element instanceof WAPlainText) {
                                        String plaintext = ((WAPlainText) element).getText();
                                        Matcher calsMatcher = CALORIES_PATTERN.matcher(plaintext);
                                        if (calsMatcher.find()) {
                                            calories = Integer.parseInt(calsMatcher.group(1));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // TODO: warnings, assumptions, other WA stuff
        } catch (WAException e) {
            e.printStackTrace();
        }

        return calories;
    }
}
