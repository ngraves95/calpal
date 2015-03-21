package fittr.io.fittr;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wolfram.alpha.WAAssumption;
import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAPod;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;
import com.wolfram.alpha.WASubpod;
import com.wolfram.alpha.impl.WAAssumptionImpl;
import com.wolfram.alpha.test.Main;

/**
 * Class that queries WA for food nutrition information. Extends the Android Async class. The
 * output is a list, with the first entry being the number of calories in the food, and the
 * second entry being a list of search suggestions.
 *
 * Created by creston on 3/21/15.
 */
public class SearchTask extends AsyncTask<String, Integer, SearchResult> {

    private static final Pattern GRAMS_PATTERN = Pattern.compile("\\(?([0-9]+) g\\)?$", Pattern.MULTILINE);
    private static final Pattern CALORIES_PATTERN = Pattern.compile("total calories\\s+([0-9]+)");
    private Button adder;
    private ListView destination;
    private Context context;
    private View source;
    private TextView errorField;
    private ProgressDialog progress;

    public SearchTask(Context context, Button adder, ListView destination, View source, TextView errorField) {
        this.adder = adder;
        this.destination = destination;
        this.context = context;
        this.source = source;
        this.errorField = errorField;
    }

    @Override
    protected void onPreExecute() {
        source.setEnabled(false);
        progress = new ProgressDialog(context);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.show();

    }


    protected SearchResult doInBackground(String... input) {

        // create and perform WA queries with this object
        WAEngine engine = new WAEngine();

        engine.setAppID(MainActivity.WA_APP_ID);
        engine.addFormat("plaintext");

        // make query
        WAQuery query = engine.createQuery();
        query.setInput(input[0]);

        // add an assumption
        if (input.length > 1) {
            query.addAssumption(input[1]);
        }

        // default results
        String food = input[0];
        boolean success = false;
        Integer grams = 0;
        Integer calories = 0;
        List<String> suggestions = new ArrayList<String>();

        try {
            System.out.println("Query URL:");
            System.out.println(engine.toURL(query));
            System.out.println();

            WAQueryResult queryResult = engine.performQuery(query);
            if (queryResult.isError()) {
                System.out.println("Query error:");
                System.out.println("   error code: " + queryResult.getErrorCode());
                System.out.println("   error message: " + queryResult.getErrorMessage());
            } else if (!queryResult.isSuccess()) {
                System.out.println("Query was not understood; no results available.");
            } else {
                // Got a result
                System.out.println("Successful query. Pods follow:\n");
                for (WAPod pod : queryResult.getPods()) {
                    if (!pod.isError()) {
                        System.out.println(pod.getTitle());
                        // find pod with input interpretation
                        if (pod.getTitle().toLowerCase().contains("input interpretation")) {
                            for (WASubpod subpod : pod.getSubpods()) {
                                for (Object element : subpod.getContents()) {
                                    if (element instanceof WAPlainText) {
                                        String plaintext = ((WAPlainText) element).getText();
                                        System.out.println("Found input interpretation:");
                                        food = plaintext.split("\\|")[0].trim();
                                        System.out.println(food);
                                    }
                                }
                            }
                        }
                        // find pod with nutrition facts
                        if (pod.getTitle().toLowerCase().contains("nutrition facts")) {
                            System.out.println("Found nutrition facts. Continuing.");
                            for (WASubpod subpod : pod.getSubpods()) {
                                for (Object element : subpod.getContents()) {
                                    if (element instanceof WAPlainText) {
                                        String plaintext = ((WAPlainText) element).getText();
                                        //System.out.println(plaintext);
                                        //System.out.println("");
                                        System.out.println("Extracted the following:");
                                        Matcher gramMatcher = GRAMS_PATTERN.matcher(plaintext);
                                        Matcher calsMatcher = CALORIES_PATTERN.matcher(plaintext);
                                        if (gramMatcher.find() && calsMatcher.find()) {
                                            System.out.println(gramMatcher.group(1));
                                            grams = Integer.parseInt(gramMatcher.group(1));
                                            System.out.println("Grams: " + grams.toString());
                                            calories = Integer.parseInt(calsMatcher.group(1));
                                            System.out.println("Calories: " + calories.toString());

                                            success = true;
                                        }
                                    }
                                }
                            }
                        }

                        System.out.println("");
                    }
                }
                System.out.println("Printing assumptions");
                for (WAAssumption assumption : queryResult.getAssumptions()) {
                    System.out.println("----------");
                    if (assumption.getType().toLowerCase().equals("clash")) {
                        if (!success) { // check for a food assumption on failure
                            for (int i = 0; i < assumption.getCount(); i++) {
                                if (assumption.getNames()[i].equals("ExpandedFood")) {
                                    // re-run the query with the new assumption
                                    return doInBackground(input[0], assumption.getInputs()[i]);
                                }
                            }
                        }
                        continue; // skip clashes
                    }
                    for (String d : assumption.getDescriptions()) {
                        System.out.println(d);
                        suggestions.add(d);
                    }
                }
                System.out.println("Success status: " + success);
            }
        // TODO: warnings, assumptions, other WA stuff
        } catch (WAException e) {
            e.printStackTrace();
        }

        // TODO: return calories
        return new SearchResult(food, grams, calories, suggestions, success);
    }

    @Override
    protected void onPostExecute(SearchResult sr) {
        List<String> items = new ArrayList<>();

        if (sr.isSuccess()) {
            adder.setVisibility(View.VISIBLE);
            adder.setText("Add " + sr.getFood() + " (" + sr.getAmount() + " g)");
        } else {
            adder.setVisibility(View.GONE);
        }

        if (sr != null) {
            List<String> suggest = sr.getSuggestions();
            if (!suggest.isEmpty()) {
                items.addAll(suggest);
                errorField.setVisibility(View.GONE);
            } else if(!sr.isSuccess()) {
                // everything failed.
                errorField.setText("Sorry, your search returned no results.");
                errorField.setVisibility(View.VISIBLE);
            } else {
                // Search was successful but no alternatives
                errorField.setVisibility(View.GONE);
            }

        }

        ArrayAdapter<String> searchResultAdapter = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1,
                items
        );


        AddResultListener rl = new AddResultListener(context, adder, searchResultAdapter, sr);
        SwapPageListener sl = new SwapPageListener(((MainActivity)context).mViewPager);
        CompositeListener compList = new CompositeListener();
        compList.add(rl);
        compList.add(sl);

        //adder.setOnClickListener(rl);
        //MainActivity act = (MainActivity) context;
        //adder.setOnClickListener(new SwapPageListener(act.mViewPager));
        adder.setOnClickListener(compList);
        destination.setAdapter(searchResultAdapter);
        source.setEnabled(true);
        // To dismiss the dialog
        progress.dismiss();

    }

}
