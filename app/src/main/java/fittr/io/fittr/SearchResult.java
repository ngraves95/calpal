package fittr.io.fittr;

import java.util.List;

/**
 * A class that wraps the search results from a WA query.
 *
 * Created by creston on 3/21/15.
 */
public class SearchResult {

    private String food;
    private int amount;
    private int calories;
    private List<String> suggestions;
    private boolean success;

    /**
     *
     * @param food The food consumed
     * @param amount The amount of food consumed in grams
     * @param calories Calories in the food
     * @param suggestions Similar search suggestions
     * @param success Did the search succeed?
     */
    public SearchResult(String food,
                             int amount,
                             int calories,
                             List<String> suggestions,
                             boolean success) {
        this.food = food;
        this.amount = amount;
        this.calories = calories;
        this.suggestions = suggestions;
        this.success = success;
    }

    public String getFood() {
        return food;
    }

    public int getAmount() {
        return amount;
    }

    public int getCalories() {
        return calories;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public boolean isSuccess() {
        return success;
    }
}
