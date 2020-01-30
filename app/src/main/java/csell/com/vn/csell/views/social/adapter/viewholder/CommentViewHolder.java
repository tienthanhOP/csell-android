package csell.com.vn.csell.views.social.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.Calendar;
import java.util.Objects;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.friend.activity.FriendDetailsActivity;
import csell.com.vn.csell.views.account.activity.PersonalPageActivity;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.TimeAgo;
import csell.com.vn.csell.models.Comment;
import csell.com.vn.csell.models.UserRetro;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 4/30/2018.
 */

public class CommentViewHolder extends RecyclerView.ViewHolder {

    public TextView txtContentComment;
    public TextView txtDate;
    public TextView txtReplyComment;
    public TextView txtUserName;
    public CircleImageView imgAvatar;
    private FileSave fileGet;
    private OnClickReply onClickReply;
    private BaseActivityTransition baseActivityTransition;

    public interface OnClickReply {
        void onClickReply(Comment comment);
    }

    public CommentViewHolder(View itemView, OnClickReply onClickReply) {
        super(itemView);
        this.onClickReply = onClickReply;
        txtContentComment = itemView.findViewById(R.id.txtContentComment);
        txtReplyComment = itemView.findViewById(R.id.txtReplyComment);
        txtDate = itemView.findViewById(R.id.txtDate);
        txtUserName = itemView.findViewById(R.id.txtUserName);
        imgAvatar = itemView.findViewById(R.id.img_avatar_user);

    }

    public void setValue(Context context, Comment comment, FileSave fileGet) {

        try {
            baseActivityTransition = new BaseActivityTransition(context);

            txtUserName.setText(comment.display_name);
            if (txtDate != null) {
                txtDate.setText(TimeAgo.toRelative(comment.date_created, Calendar.getInstance().getTimeInMillis(), 1));
            }

            if (comment.is_reply != null && comment.is_reply) {
                if (TextUtils.isEmpty(comment.reply_display_name)) {
                    txtContentComment.setText(comment.content + "");
                } else {

                    SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                    stringBuilder.append(comment.reply_display_name.trim());
                    stringBuilder.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {

                            if (!comment.reply_uid.equals(fileGet.getUserId())) {

                                UserRetro friend = new UserRetro();
                                friend.setUid(comment.reply_uid);
                                friend.setDisplayname(comment.reply_display_name);
                                Intent detail = new Intent(context, FriendDetailsActivity.class);
                                detail.putExtra(Constants.KEY_PASSINGDATA_FRIEND_ID, friend);
                                detail.putExtra(Constants.KEY_PASSINGDATA_SELECT_REPLY_COMMENT_USER, true);
                                baseActivityTransition.transitionTo(detail, 0);
                            } else {
                                Intent detail = new Intent(context, PersonalPageActivity.class);
                                baseActivityTransition.transitionTo(detail, 0);
                            }
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(context.getResources().getColor(R.color.dark_blue_50));
                            ds.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                            ds.setUnderlineText(false);
                        }
                    }, 0, comment.reply_display_name.trim().length(), 0);

                    stringBuilder.append(" ");
                    stringBuilder.append(comment.content);

                    txtContentComment.setMovementMethod(LinkMovementMethod.getInstance());
                    txtContentComment.setText(stringBuilder);
                }
            } else {
                txtContentComment.setText(comment.content + "");
            }


            GlideApp.with(context)
                    .load(!TextUtils.isEmpty(comment.avatar) ? comment.avatar : R.drawable.ic_logo)
                    .placeholder(R.drawable.ic_logo)
                    .error(R.drawable.ic_logo)
                    .into(imgAvatar);

            txtReplyComment.setOnClickListener(v -> {
                try {
                    onClickReply.onClickReply(comment);
//                    ((CommentDialog) context).setReplyComment(comment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


            txtUserName.setOnClickListener(v -> {
                nextDetailFriend(comment, context);
            });

            imgAvatar.setOnClickListener(v -> {
                nextDetailFriend(comment, context);
            });

            try {
                if (comment.uid.equals(fileGet.getUserId())) {
                    txtReplyComment.setVisibility(View.GONE);
                } else {
                    txtReplyComment.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {

            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d(getClass().getSimpleName(), e.getMessage());
        }
    }


    private void nextDetailFriend(Comment comment, Context context) {
        try {
            fileGet = new FileSave(context, Constants.GET);
            String userId = fileGet.getUserId();
            if (Objects.equals(userId, comment.uid)) {
                FriendDetailsActivity.friend = null;
                Intent intent = new Intent(context, PersonalPageActivity.class);
                baseActivityTransition.transitionTo(intent, 0);
            } else {
                Intent detail = new Intent(context, FriendDetailsActivity.class);
                UserRetro friend = new UserRetro();
                friend.setUid(comment.uid);
                friend.setDisplayname(comment.display_name);
                detail.putExtra(Constants.KEY_PASSINGDATA_FRIEND_ID, friend);
                baseActivityTransition.transitionTo(detail, 0);
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

}
