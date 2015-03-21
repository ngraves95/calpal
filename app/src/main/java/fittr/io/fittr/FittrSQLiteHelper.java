package fittr.io.fittr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by creston on 3/21/15.
 */
public class FittrSQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_FOODS = "foods";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_FOOD = "food";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_CALORIES = "calories";
    
    private static final String DATABASE_NAME = "fittr";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_FOODS + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TIMESTAMP + " int not null, "
            + COLUMN_FOOD + " text not null, "
            + COLUMN_AMOUNT + " integer not null, "
            + COLUMN_CALORIES + " integer not null);";

    public FittrSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(FittrSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion
                        + ", which will destroy old data.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOODS);
        onCreate(db);
    }
}
