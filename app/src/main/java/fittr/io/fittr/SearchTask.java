package fittr.io.fittr;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

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

/**
 * Class that queries WA for food nutrition information. Extends the Android Async class. The
 * output is a list, with the first entry being the number of calories in the food, and the
 * second entry being a list of search suggestions.
 *
 * Created by creston on 3/21/15.
 */
public class SearchTask extends AsyncTask<String, Integer, SearchResult> {

    // TODO: remove this from git
    private static String appid = "G5Q47V-QGJ6JYKHUP";
    private static final Pattern GRAMS_PATTERN = Pattern.compile("serving size .+ \\(([0-9]+) g\\)");
    private static final Pattern CALORIES_PATTERN = Pattern.compile("total calories  ([0-9]+)");
    private ListView destination;
    private Context context;
    private View source;

    public SearchTask(Context context, ListView destination, View source) {
        this.destination = destination;
        this.context = context;
        this.source = source;

    }

    @Override
    protected void onPreExecute() {
        source.setEnabled(false);

    }


    protected SearchResult doInBackground(String... input) {

        // create and perform WA queries with this object
        WAEngine engine = new WAEngine();

        engine.setAppID(appid);
        engine.addFormat("plaintext");

        // make query
        WAQuery query = engine.createQuery();
        query.setInput(input[0]);

        // default results
        String food = input[0];
        boolean success = true;
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
                success = false;
            } else if (!queryResult.isSuccess()) {
                System.out.println("Query was not understood; no results available.");
                success = false;
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
                                        if (gramMatcher.find()) {
                                            grams = Integer.parseInt(gramMatcher.group(1));
                                            System.out.println("Grams: " + grams.toString());
                                        }
                                        if (calsMatcher.find()) {
                                            calories = Integer.parseInt(calsMatcher.group(1));
                                            System.out.println("Calories: " + calories.toString());
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
                        continue; // skip clashes
                    }
                    for (String d : assumption.getDescriptions()) {
                        System.out.println(d);
                        suggestions.add(d);
                    }
                }
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

        if (sr != null) {
            List<String> suggest = sr.getSuggestions();
            if (!sr.getSuggestions().isEmpty()) {
                items = suggest;
            } else {
                items.add(sr.getFood());
                items.add(((Integer) sr.getAmount()).toString());
                items.add(((Integer) sr.getCalories()).toString());
            }

        }

        ArrayAdapter<String> searchResultAdapter = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1,
                items
        );

        destination.setAdapter(searchResultAdapter);
        source.setEnabled(true);
    }

}
