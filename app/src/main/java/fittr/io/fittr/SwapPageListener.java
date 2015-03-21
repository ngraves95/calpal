package fittr.io.fittr;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by ngraves3 on 3/21/15.
 */
public class SwapPageListener implements View.OnClickListener {

    private ViewPager vp;

    public SwapPageListener(ViewPager pager) {
        this.vp = pager;
    }


    @Override
    public void onClick(View view) {
        int currentIndex = vp.getCurrentItem();
        vp.setCurrentItem(1-currentIndex);
    }
}
