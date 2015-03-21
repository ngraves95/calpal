package fittr.io.fittr;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

/**
 * Created by ngraves3 on 3/21/15.
 */
public class CalorieTextWatcher implements TextWatcher{

    private int surplus;
    private int deficiency;
    private TextView source;
    private TextView value;

    public CalorieTextWatcher(TextView source, TextView value) {
        this.source = source;
        this.value = value;
        surplus = Color.parseColor("#FF4747");
        deficiency = Color.parseColor("#008A2E");
    }

    public void colorText() {
        if (source != null && value != null) {
            System.out.println("Coloring text");
            int calCount = Integer.parseInt(value.getText().toString());
            if (calCount > 0) {
                source.setText("Net Calories Consumed: ");
                value.setTextColor(surplus);
            } else {
                source.setText("Net Calories Burned: ");
                value.setTextColor(deficiency);
            }
        }

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        colorText();
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        colorText();
    }

    @Override
    public void afterTextChanged(Editable editable) {
        colorText();
    }
}
