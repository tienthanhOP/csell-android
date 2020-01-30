package csell.com.vn.csell.models;

import com.google.firebase.database.ServerValue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import csell.com.vn.csell.constants.EntityFirebase;

/**
 * Created by cuong.nv on 3/7/2018.
 */

public class Comment implements Serializable {

    public String comment_id;
    public String avatar;
    public String content;
    public Long date_created;
    public String display_name;
    public Boolean is_deleted;
    public String product_id;
    public String uid;
//    public String UserName;
    public Boolean IsNoticed;
    public Boolean is_reply;
    public String reply_uid;
//    public String ReplyUserName;
    public String reply_display_name;

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(EntityFirebase.FieldAvatar, avatar);
        result.put(EntityFirebase.FieldDisplayName, display_name);
        result.put(EntityFirebase.FieldCommentContent, content);
        result.put(EntityFirebase.FieldDateCreated, ServerValue.TIMESTAMP);
        result.put(EntityFirebase.FieldIsDeleted, is_deleted);
        result.put(EntityFirebase.FieldProductId, product_id);
        result.put(EntityFirebase.FieldUserId, uid);
//        result.put(EntityFirebase.FieldUserName, UserName);
        return result;
    }

    public Map<String, Object> toMapReply() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(EntityFirebase.FieldAvatar, avatar);
        result.put(EntityFirebase.FieldDisplayName, display_name);
        result.put(EntityFirebase.FieldCommentContent, content);
        result.put(EntityFirebase.FieldDateCreated, ServerValue.TIMESTAMP);
        result.put(EntityFirebase.FieldIsDeleted, is_deleted);
        result.put(EntityFirebase.FieldProductId, product_id);
        result.put(EntityFirebase.FieldUserId, uid);
//        result.put(EntityFirebase.FieldUserName, UserName);
        result.put(EntityFirebase.FieldIsReply, is_reply);
        result.put(EntityFirebase.FieldReplyUserId, reply_uid);
//        result.put(EntityFirebase.FieldReplyUserName, ReplyUserName);
        result.put(EntityFirebase.FieldReplyDisplayName, reply_display_name);
        return result;
    }


}
