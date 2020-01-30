package csell.com.vn.csell.views.social.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import csell.com.vn.csell.R;

public class ProgressLoadmoreViewHolder  extends RecyclerView.ViewHolder{
    public ProgressBar progressBar;

    public ProgressLoadmoreViewHolder(View view) {
        super(view);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_bottom);
    }
}
