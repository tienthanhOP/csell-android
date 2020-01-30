package csell.com.vn.csell.views.social.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.models.Comment;
import csell.com.vn.csell.mycustoms.CommentDialog;
import csell.com.vn.csell.sqlites.SQLFriends;
import csell.com.vn.csell.views.social.adapter.viewholder.CommentViewHolder;

/**
 * Created by cuong.nv on 3/7/2018.
 */

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements CommentViewHolder.OnClickReply {

    private Context mContext;
    private ArrayList<Comment> data;
    private int layoutId;
    private FileSave fileGet;
    private CommentDialog commentDialog;

    public CommentsAdapter(Context context, ArrayList<Comment> data, int layout, CommentDialog commentDialog) {
        this.mContext = context;
        this.data = data;
        this.layoutId = layout;
        this.commentDialog = commentDialog;
        fileGet = new FileSave(context, Constants.GET);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new CommentViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            CommentViewHolder holderItem = (CommentViewHolder) holder;
            holderItem.setValue(mContext, data.get(position), fileGet);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }


    @Override
    public void onClickReply(Comment comment) {
        if (commentDialog != null)
            commentDialog.setReplyComment(comment);
    }
}
