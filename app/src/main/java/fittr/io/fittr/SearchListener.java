package fittr.io.fittr;

/**
 * Created by ngraves3 on 3/21/15.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Listener that executes the searching.
 */
public class SearchListener implements View.OnClickListener {

    private Context context;
    private ListView destination;
    private EditText source;

    /**
     * Constructor for SearchListener
     * @param context the context (the parent container)
     * @param destination the place to put the results of executing the call
     */
    public SearchListener(Context context, ListView destination, EditText source) {
        this.context = context;
        this.destination = destination;
        this.source = source;
    }


    @Override
    public void onClick(View view) {
        SearchResult results = search(source.getText().toString());

        List<String> items = new ArrayList<>();
        items.add("ERROR");

        if (results != null) {
            items = results.getSuggestions();
        }

        ArrayAdapter<String> searchResultAdapter = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1,
                items
        );

        destination.setAdapter(searchResultAdapter);
    }

    /**
     * Executes the SearchTask with the input query
     * @param query the String to search for
     * @return a SearchResult object of the query
     */
    private SearchResult search(String query) {

        AsyncTask<String, Integer, SearchResult> task = new SearchTask().execute(query);
        SearchResult result = null;

        try {
            result = task.get();
        } catch (InterruptedException i) {
            // Eat exception om nom nom
        } catch (ExecutionException e) {
            // Eat more exceptions nom nom nom
        }

        return result;
    }
}
