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
    public void addFood(String food, int amount, int calories) {
        ContentValues values = new ContentValues();
        values.put(FittrSQLiteHelper.COLUMN_FOOD, food);
        values.put(FittrSQLiteHelper.COLUMN_AMOUNT, amount);
        values.put(FittrSQLiteHelper.COLUMN_CALORIES, calories);
        values.put(FittrSQLiteHelper.COLUMN_TIMESTAMP, System.currentTimeMillis() / 1000L);
        db.insert(FittrSQLiteHelper.TABLE_FOODS, null, values);
    }

    public String deleteMatchingFood(String food, String calories) {
        Cursor cursor = db.query(FittrSQLiteHelper.TABLE_FOODS, allColumns,
                null, null, null, null, null
        );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            System.out.println("Food: " + food + "|");
            System.out.println("Act.: " + cursor.getString(4) + "|");
            System.out.println("Calories: " + calories + "|");
            System.out.println("Actual..: " + cursor.getString(2) + "|");
            if (food.equals(cursor.getString(4)) && calories.equals(cursor.getString(2))) {
                String rowId = cursor.getString(0);
                db.delete(FittrSQLiteHelper.TABLE_FOODS, FittrSQLiteHelper.COLUMN_ID + '=' + rowId, null);
                return rowId;
            }

            cursor.moveToNext();
        }

        cursor.close();

        return null;
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
        List<String> out = new ArrayList<>();

        long minTimeStamp = Util.dayStartMillis() / 1000L;
        long maxTimeStamp = Util.dayEndmillis() / 1000L;
        Cursor cursor = db.query(FittrSQLiteHelper.TABLE_FOODS, allColumns,
                "timestamp >= " + minTimeStamp + " AND timestamp <= " + maxTimeStamp,
                null, null, null, null
        );

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String food = cursor.getString(4) + "\t\t" + cursor.getString(2) + " cal\t\t(" +cursor.getString(1) + " g)" ;
            out.add(food);
            cursor.moveToNext();
        }
        cursor.close();
        return out;
    }

    public int getCalorieCountAtDate(Calendar date) {
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

        int calorieTotal = 0;

        long minTimeStamp = minTime.getTimeInMillis() / 1000L;
        long maxTimeStamp = maxTime.getTimeInMillis() / 1000L;
        Cursor cursor = db.query(FittrSQLiteHelper.TABLE_FOODS, allColumns,
                "timestamp >= " + minTimeStamp + " AND timestamp <= " + maxTimeStamp,
                null, null, null, null
        );

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String calories = cursor.getString(2);
            calorieTotal += Integer.parseInt(calories);
            cursor.moveToNext();
        }
        cursor.close();
        return calorieTotal;
    }
}
