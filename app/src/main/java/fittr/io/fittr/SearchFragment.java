package fittr.io.fittr;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by ngraves3 on 3/21/15.
 */
public class SearchFragment extends Fragment {

    ListView searchResultView;
    Button searchButton;
    Button adder;
    EditText query;

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
        adder = (Button) view.findViewById(R.id.queryResult);
        searchResultView = (ListView) view.findViewById(R.id.searchResults);
        query = (EditText) view.findViewById(R.id.foodQuery);

        query.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                EditText txtName = (EditText) getActivity().findViewById(R.id.foodQuery);
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(txtName, InputMethodManager.SHOW_IMPLICIT);
                //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                query.setTextColor(Color.parseColor("#000000"));
                query.setText("");
            }
        });

        TextView errorField = (TextView) view.findViewById(R.id.errorField);
        SearchListener sl = new SearchListener(context, adder, searchResultView, query, errorField);
        searchButton.setOnClickListener(sl);
        searchResultView.setOnItemClickListener(sl);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.search, container, false);
    }
}

