package fittr.io.fittr;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 * Created by ngraves3 on 3/21/15.
 */
public class SearchFragment extends Fragment {

    ListView searchResultView;
    Button searchButton;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private Context context;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SearchFragment newInstance(int sectionNumber, Context context) {

        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        fragment.setContext(context);
        return fragment;
    }

    public SearchFragment() {
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        searchButton = (Button) view.findViewById(R.id.searchButton);
        searchResultView = (ListView) view.findViewById(R.id.searchResults);
        EditText query = (EditText) view.findViewById(R.id.foodQuery);

        searchButton.setOnClickListener(new SearchListener(
                        context,
                        searchResultView,
                        query
                )
        );

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.search, container, false);
    }
}

