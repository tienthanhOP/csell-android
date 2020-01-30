package csell.com.vn.csell.models;

import java.io.Serializable;

public class LastMessage implements Serializable {

    public String room_id;
    public Boolean is_accepted;
    public Boolean is_pending;
    public Boolean is_seen;
    public String message_content;
    public Long last_date;
    public String message_id;
    public String receiver;
    public String receiver_avatar;
    public String receiver_name;
    public String sender;
    public String sender_avatar;
    public String sender_name;
    public int count_unread;

}
