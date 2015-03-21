package fittr.io.fittr;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by ngraves3 on 3/21/15.
 */
public class SearchActivity extends ActionBarActivity {

    private ListView searchResultView;
    private Button searchButton;

    protected void onCreate(Bundle savedInstanceState) {
        searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new SearchListener(this));
        searchResultView = (ListView) findViewById(R.id.searchResults);
    }

    private SearchResult search(String query) {
        // test
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

    /**
     * Listener that executes the searching.
     */
    private class SearchListener implements View.OnClickListener {

        private Context context;

        public SearchListener(Context context) {
            this.context = context;
        }


        @Override
        public void onClick(View view) {
            EditText query = (EditText) findViewById(R.id.foodQuery);
            SearchResult results = search(query.getText().toString());

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

            searchResultView.setAdapter(searchResultAdapter);

        }
    }


}
