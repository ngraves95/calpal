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
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.result.DataReadResult;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Gets data from Google Fit
 *
 * Created by creston on 3/21/15.
 */
public class CalorieTask extends AsyncTask<Void, Void, Integer> {

    private Context context;
    private Calendar date;

    public CalorieTask(Context context, Calendar date) {
        this.context = context;
        this.date = date;
    }

    protected Integer doInBackground(Void... input) {
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

        GoogleApiClient client = new GoogleApiClient.Builder(context)
                .addApi(Fitness.HISTORY_API)
                .build();

        PendingResult<DataReadResult> pendingResult = Fitness.HistoryApi.readData(
                client,
                new DataReadRequest.Builder()
                    .read(DataType.TYPE_CALORIES_EXPENDED)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .build());

        DataReadResult readDataResult = pendingResult.await();
        DataSet dataSet = readDataResult.getDataSet(DataType.TYPE_CALORIES_EXPENDED);

        for (DataPoint p : dataSet.getDataPoints()) {
            System.out.println(p.toString());
        }

        return new Integer(0);
    }

}
