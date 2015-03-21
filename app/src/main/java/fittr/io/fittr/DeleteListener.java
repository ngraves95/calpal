package fittr.io.fittr;

import android.app.LauncherActivity;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ngraves3 on 3/21/15.
 */
public class DeleteListener implements ListView.OnItemLongClickListener{

    private ListView container;
    private Context context;
    private List<String> items;
    private TextView calCount;


    public DeleteListener(Context context, ListView container, List<String> items, TextView calCount) {
        this.container = container;
        this.context= context;
        this.items = items;
        this.calCount = calCount;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        FoodModel model = new FoodModel(context);
        try {
            System.out.println("Registering long lick");
            String listEntry = (String) adapterView.getItemAtPosition(i);
            Pattern findCalories = Pattern.compile("(.*?)([0-9]+).*");
            Matcher matcher = findCalories.matcher(listEntry);

            String[] foodQuant = new String[2];
            if (matcher.find()) {
                foodQuant[0] = matcher.group(1);
                foodQuant[1] = matcher.group(2);
            }

            for (int j = 0; j < foodQuant.length; j++) {
                foodQuant[j] = foodQuant[j].trim();
                foodQuant[j] = foodQuant[j].replace("\t", "");
            }

            System.out.println("Food name: " + foodQuant[0]);
            System.out.println("Quantity: " + foodQuant[1]);

            model.open();

            String retval = model.deleteMatchingFood(foodQuant[0], foodQuant[1]);

            model.close();

            if (retval != null) {
                items.remove(i);
                System.out.println("Removing an item from model");
                int calories = Integer.parseInt(foodQuant[1]);
                int newCalories = Integer.parseInt(calCount.getText().toString()) - calories;
                calCount.setText(newCalories + "");
                ((ArrayAdapter) adapterView.getAdapter()).notifyDataSetChanged();
            }

            return retval != null;

        } catch (SQLException sqle) {
            // Eat exception
            return false;
        }
    }
}