package csell.com.vn.csell.models;

import com.google.firebase.database.ServerValue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import csell.com.vn.csell.constants.EntityFirebase;

/**
 * Created by cuong.nv on 4/6/2018.
 */

public class Message implements Serializable {

    public Long date_created;
    public String message_content;
    public String receiver;
    public String receiver_name;
    public String receiver_avatar;
    public String sender;
    public String sender_avatar;
    public String sender_name;
    public Product product;
    public Boolean is_pending;
    public String message_id;
    public Boolean is_accepted;
    public Boolean is_sent;
    public Boolean is_seen;
    public int count_unread;

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(EntityFirebase.FieldDateCreated, ServerValue.TIMESTAMP);
        result.put(EntityFirebase.FieldMessageContent, message_content);
        result.put(EntityFirebase.FieldIsSeen, false);
        result.put(EntityFirebase.FieldReceiver, receiver);
        result.put(EntityFirebase.FieldReceiverName, receiver_name);
        result.put(EntityFirebase.FieldReceiverAvatar, receiver_avatar);
        result.put(EntityFirebase.FieldSender, sender);
        result.put(EntityFirebase.FieldSenderName, sender_name);
        result.put(EntityFirebase.FieldSenderAvatar, sender_avatar);
        result.put(EntityFirebase.FieldCountUnread, count_unread);
        return result;
    }

    public Map<String, Object> toLastMessageMeMap(boolean is_pending) {
        HashMap<String, Object> result = new HashMap<>();
//        result.put(EntityFirebase.FieldLastDate, FieldValue.serverTimestamp());
        result.put(EntityFirebase.FieldDateCreated, ServerValue.TIMESTAMP);
        result.put(EntityFirebase.FieldMessageContent, message_content);
        result.put(EntityFirebase.FieldIsSeen, is_seen);
        result.put(EntityFirebase.FieldReceiver, receiver);
        result.put(EntityFirebase.FieldReceiverName, receiver_name);
        result.put(EntityFirebase.FieldReceiverAvatar, receiver_avatar);
        result.put(EntityFirebase.FieldSender, sender);
        result.put(EntityFirebase.FieldSenderName, sender_name);
        result.put(EntityFirebase.FieldSenderAvatar, sender_avatar);
        result.put(EntityFirebase.FieldIsPending, is_pending);
        result.put(EntityFirebase.FieldMessageId, message_id);
        result.put(EntityFirebase.FieldCountUnread, count_unread);
        result.put(EntityFirebase.FieldIsAccepted, is_accepted);
        if (product != null) {
            result.put(EntityFirebase.FieldProduct, product);
        }
        return result;
    }

    public Map<String, Object> addMessage() {
        HashMap<String, Object> data = new HashMap<>();

        data.put(EntityFirebase.FieldDateCreated, ServerValue.TIMESTAMP);
        data.put(EntityFirebase.FieldMessageContent, message_content);
        data.put(EntityFirebase.FieldReceiver, receiver);
        data.put(EntityFirebase.FieldReceiverName, receiver_name);
        data.put(EntityFirebase.FieldReceiverAvatar, receiver_avatar);
        data.put(EntityFirebase.FieldSender, sender);
        data.put(EntityFirebase.FieldSenderAvatar, sender_avatar);
        data.put(EntityFirebase.FieldSenderName, sender_name);
        data.put(EntityFirebase.FieldIsPending, is_pending);
        data.put(EntityFirebase.FieldMessageId, message_id);
        data.put(EntityFirebase.FieldIsSent, is_sent);
        data.put(EntityFirebase.FieldIsSeen, is_seen);
        data.put(EntityFirebase.FieldCountUnread, count_unread);
        if (product != null) {
            data.put(EntityFirebase.FieldProduct, product);
        }
        return data;
    }

}
