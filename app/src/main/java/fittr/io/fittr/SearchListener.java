package fittr.io.fittr;

/**
 * Created by ngraves3 on 3/21/15.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Listener that executes the searching.
 */
public class SearchListener implements View.OnClickListener, ListView.OnItemClickListener {

    private Context context;
    private Button adder;
    private ListView destination;
    private EditText source;
    private TextView errorField;

    /**
     * Constructor for SearchListener
     * @param context the context (the parent container)
     * @param destination the place to put the results of executing the call
     */
    public SearchListener(Context context, Button adder, ListView destination, EditText source, TextView errorField) {
        this.context = context;
        this.adder = adder;
        this.destination = destination;
        this.source = source;
        this.errorField = errorField;
    }


    @Override
    public void onClick(View view) {
        String query = source.getText().toString();
        search(query, view);
    }

    /**
     * Executes the SearchTask with the input query
     * @param trigger the view that was clicked
     * @param query the String to search for
     * @return a SearchResult object of the query
     */
    private void search(String query, View trigger) {

        AsyncTask<String, Integer, SearchResult> task = new SearchTask(context, adder, destination, trigger, errorField).execute(query);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String query = (String) adapterView.getItemAtPosition(i);

        if (adapterView.getAdapter().getCount() == 1) {
            FoodModel model = new FoodModel(context);
        }

        source.setText(query);
        ((ArrayAdapter<String>) destination.getAdapter()).clear();
        adder.setVisibility(View.GONE);

        search(query, adapterView);
    }
}
