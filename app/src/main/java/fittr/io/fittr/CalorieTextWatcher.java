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
        surplus = Color.parseColor("#c12300");//"#FF4747");
        deficiency = Color.parseColor("#bad107");//"#008A2E");
    }

    public void colorText() {
        if (source != null && value != null) {
            System.out.println("Coloring text");
            int calCount;
            try {
                calCount=Integer.parseInt(value.getText().toString());
            } catch (NumberFormatException nfe){
                source.setBackgroundColor(surplus);
                value.setBackgroundColor(surplus);
                return;
            }
            source.setText("Net Calories Consumed: ");
            if (calCount > 2000) {
                source.setBackgroundColor(surplus);
                value.setBackgroundColor(surplus);

            } else {
                source.setBackgroundColor(deficiency);
                value.setBackgroundColor(deficiency);
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
