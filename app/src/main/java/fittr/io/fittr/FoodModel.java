package fittr.io.fittr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by creston on 3/21/15.
 */
public class FoodModel {

    private SQLiteDatabase db;
    private FittrSQLiteHelper dbHelper;
    private String[] allColumns = {
            FittrSQLiteHelper.COLUMN_ID,
            FittrSQLiteHelper.COLUMN_AMOUNT,
            FittrSQLiteHelper.COLUMN_CALORIES,
            FittrSQLiteHelper.COLUMN_TIMESTAMP,
            FittrSQLiteHelper.COLUMN_FOOD,
    };

    public FoodModel(Context context) {
        dbHelper = new FittrSQLiteHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Inserts food with the current timestamp.
     *
     * @param food The name of the food to add
     * @param amount The amount of food in grams to add
     */
    public void addFood(String food, int amount) {
        ContentValues values = new ContentValues();
        values.put(FittrSQLiteHelper.COLUMN_FOOD, food);
        values.put(FittrSQLiteHelper.COLUMN_AMOUNT, amount);
        values.put(FittrSQLiteHelper.COLUMN_TIMESTAMP, System.currentTimeMillis() / 1000L);
        db.insert(FittrSQLiteHelper.TABLE_FOODS, null, values);
    }

    /**
     * Delete food with the given id.
     *
     * @param id
     */
    public void deleteFood(String id) {
        db.delete(FittrSQLiteHelper.TABLE_FOODS, FittrSQLiteHelper.COLUMN_ID + '=' + id, null);
    }

    public List<String> getFoodsAtDate(Calendar date) {
        Calendar minTime = Calendar.getInstance();
        minTime.set(
            date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH),
            0, 0, 0
        );
        Calendar maxTime = Calendar.getInstance();
        maxTime.set(
                date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH),
                24, 0, 0
        );

        List<String> out = new ArrayList<>();

        long minTimeStamp = minTime.getTimeInMillis() / 1000L;
        long maxTimeStamp = maxTime.getTimeInMillis() / 1000L;
        Cursor cursor = db.query(FittrSQLiteHelper.TABLE_FOODS, allColumns,
                "timestamp >= " + minTimeStamp + " AND timestamp <= " + maxTimeStamp,
                null, null, null, null
        );

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String food = cursor.getString(4);
            out.add(food);
            cursor.moveToNext();
        }
        cursor.close();
        return out;

    }
}
