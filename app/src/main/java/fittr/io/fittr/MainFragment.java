package fittr.io.fittr;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ngraves3 on 3/21/15.
 *
 * Main fragment containing a view of the activities and meals.
 */
public class MainFragment extends Fragment {

    ListView mealsData;
    ArrayAdapter<String> mealsDataAdapter;

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
    public static MainFragment newInstance(int sectionNumber, Context context) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        fragment.setContext(context);
        return fragment;
    }

    public MainFragment() {
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mealsData = (ListView) view.findViewById(R.id.mealsData);
        FoodModel model = new FoodModel(context);
        List<String> items;
        try  {
            model.open();
            items = model.getFoodsAtDate(Calendar.getInstance());

            mealsDataAdapter = new ArrayAdapter<String>(
                    context,
                    android.R.layout.simple_list_item_1,
                    items
            );

            mealsData.setAdapter(mealsDataAdapter);
            model.close();

        } catch (SQLException e) {
            System.out.println("Something went wrong...");
        }

    }

    @Override
    public void onResume() {
        if (mealsDataAdapter != null) {
            mealsDataAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main, container, false);
    }
}
