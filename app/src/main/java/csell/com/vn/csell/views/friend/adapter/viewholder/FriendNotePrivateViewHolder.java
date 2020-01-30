package csell.com.vn.csell.views.friend.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.EntityFirebase;

/**
 * Created by chuc.nq on 5/3/2018.
 */

public class FriendNotePrivateViewHolder extends RecyclerView.ViewHolder {

    TextView tvName;

    public FriendNotePrivateViewHolder(View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tv_name);
    }

    public void setValue(HashMap<String, Object> map){
        tvName.setText(map.get(EntityFirebase.FieldDisplayName).toString());
    }

}
