package fittr.io.fittr;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.sql.SQLException;

/**
 * Created by creston on 3/21/15.
 */
public class AddResultListener implements View.OnClickListener {

    private Context context;
    private SearchResult result;
    private ArrayAdapter<String> adapter;
    private Button adder;

    public AddResultListener(Context context, Button adder, ArrayAdapter<String> adapter, SearchResult result) {
        this.context = context;
        this.result = result;
        this.adder = adder;
        this.adapter = adapter;
    }

    @Override
    public void onClick(View view) {
        FoodModel model = new FoodModel(context);
        try {
            model.open();
            System.out.println("INSERTING INTO DATABASE");
            System.out.println("\tFood: " + result.getFood() + "\n\tAmount: " + result.getAmount()
                    + "\n\tCalories: " + result.getCalories());
            model.addFood(result.getFood(), result.getAmount(), result.getCalories());
            model.close();

            // reset search
            this.adder.setVisibility(View.GONE);
            this.adapter.clear();
        } catch (SQLException e) {
            System.out.println("Something went wrong");
        }
    }
}