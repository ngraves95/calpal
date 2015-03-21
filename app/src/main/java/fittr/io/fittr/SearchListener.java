package fittr.io.fittr;

/**
 * Created by ngraves3 on 3/21/15.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Listener that executes the searching.
 */
public class SearchListener implements View.OnClickListener, ListView.OnItemClickListener {

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
        String query = source.getText().toString();
        search(query, view);
    }

    /**
     * Executes the SearchTask with the input query
     * @param query the String to search for
     * @return a SearchResult object of the query
     */
    private void search(String query, View trigger) {

        AsyncTask<String, Integer, SearchResult> task = new SearchTask(context, destination, trigger).execute(query);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String query = (String) adapterView.getItemAtPosition(i);
        search(query, adapterView);
    }
}
