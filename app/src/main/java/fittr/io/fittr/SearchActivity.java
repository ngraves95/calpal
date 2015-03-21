package fittr.io.fittr;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by ngraves3 on 3/21/15.
 */
public class SearchActivity extends ActionBarActivity {
    ListView searchResultView;
    Button searchButton;
    Button adderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        searchButton = (Button) findViewById(R.id.searchButton);
        adderButton = (Button) findViewById(R.id.queryResult);
        searchResultView = (ListView) findViewById(R.id.searchResults);
        EditText query = (EditText) findViewById(R.id.foodQuery);
        TextView errorField = (TextView) findViewById(R.id.errorField);
        searchButton.setOnClickListener(new SearchListener(
                        this,
                        adderButton,
                        searchResultView,
                        query,
                        errorField
                )
        );

    }
}
