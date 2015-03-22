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

import java.sql.SQLException;
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
public class CalorieTask extends AsyncTask<Boolean, Void, Integer> {

    private GoogleApiClient mClient;
    private long startTime;
    private long endTime;

    public static final Pattern CALORIES_PATTERN = Pattern.compile("([0-9]+) Cal");

    private static int lastSteps = 0;
    private static int lastCals = 0;

    private TextView destination;

    public CalorieTask(GoogleApiClient mClient, long startTime, long endTime, TextView destination) {
        this.mClient = mClient;
        this.startTime = startTime;
        this.endTime = endTime;
        this.destination = destination;
    }

    protected Integer doInBackground(Boolean... input) {

        System.out.println("Getting Calorie data....");

        Person me = Plus.PeopleApi.getCurrentPerson(mClient);

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
        boolean male = me.hasGender() && me.getGender() == Person.Gender.MALE;

        try {
            for (DataPoint dp : readResult.getBuckets().get(0).getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA).getDataPoints()) {
                for (Field field : dp.getDataType().getFields()) {
                    if (field.getName().toLowerCase().equals("steps")) {
                        steps += dp.getValue(field).asInt();
                    }
                }
            }
            // TODO: get accurate speed data
            DataPoint wp = weightReadResult.getDataSet(DataType.TYPE_WEIGHT).getDataPoints().get(0);
            weight = wp.getValue(wp.getDataType().getFields().get(0)).asFloat();
        } catch(IndexOutOfBoundsException e) {
            // lazy man's way out of this problem
        }

        // avoid querying WA if possible
        if (!input[0] && lastSteps == steps) {
            System.out.println("Keeping last calorie count.");
            return lastCals;
        }

        int calories = calcCalories(weight, steps, male);

        System.out.println("Counted " + steps + " steps which burns " + calories + " calories.");
        System.out.println("Average weight " + weight);

        lastSteps = steps;
        lastCals = calories;

        return calories;

    }

    @Override
    protected void onPreExecute() {
        destination.setText("Calculating...");
    }

    @Override
    protected void onPostExecute(Integer calsBurned) {
        int currentCalCount = 0;
        currentCalCount = -calsBurned;
        FoodModel model = new FoodModel(mClient.getContext());
        try {
            model.open();

            currentCalCount += model.getCalorieCountAtDate(Util.now());
        } catch (SQLException e) {

        }
        destination.setText(currentCalCount + "");
    }

    private int calcCalories(float weight, int steps, boolean male) {

        // create and perform WA queries with this object
        WAEngine engine = new WAEngine();

        engine.setAppID(MainActivity.WA_APP_ID);
        engine.addFormat("plaintext");

        // make query
        String gender = male ? "male" : "female";
        WAQuery query = engine.createQuery();
        query.setInput("calories burned from a " + weight + "kg " + gender + " walking " + steps + "steps");

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
