package fittr.io.fittr;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 * Created by ngraves3 on 3/21/15.
 */
public class SearchActivity extends ActionBarActivity {
    ListView searchResultView;
    Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        searchButton = (Button) findViewById(R.id.searchButton);
        searchResultView = (ListView) findViewById(R.id.searchResults);
        EditText query = (EditText) findViewById(R.id.foodQuery);
        searchButton.setOnClickListener(new SearchListener(
                        this,
                        searchResultView,
                        query
                )
        );
    }
}
