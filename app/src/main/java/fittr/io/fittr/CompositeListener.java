package fittr.io.fittr;

import android.view.View;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ngraves3 on 3/21/15.
 */
public class CompositeListener implements View.OnClickListener {

    List<View.OnClickListener> listeners;

    public CompositeListener() {
        listeners = new LinkedList<>();
    }

    public void add(View.OnClickListener ocl) {
        listeners.add(ocl);
    }

    @Override
    public void onClick(View view) {
        for (View.OnClickListener ocl : listeners) {
            ocl.onClick(view);
        }
    }
}
