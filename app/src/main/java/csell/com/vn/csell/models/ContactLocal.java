package csell.com.vn.csell.models;

/**
 * Created by chuc.nq on 3/12/2018.
 */

public class ContactLocal {

    private int id;
    private String ContactName;
    private String Email;
    private String Phone1;
    private String Phone2;
    private boolean isAdded;
    private boolean isDeleted;
    private String hashtag;

    public boolean isSelectedGroup = false;

    public ContactLocal() {
        this.id = id;
        this.ContactName = ContactName;
        this.Email = Email;
        this.Phone1 = Phone1;
        this.Phone2 = Phone2;
        this.isAdded = isAdded;
        this.isDeleted = isDeleted;
        this.hashtag = hashtag;
    }

    public ContactLocal(int id, String ContactName, String email,
                        String phone1, String phone2,
                        boolean isAdded, boolean isDeleted) {
        this.id = id;
        this.ContactName = ContactName;
        this.Email = email;
        this.Phone1 = phone1;
        this.Phone2 = phone2;
        this.isAdded = isAdded;
        this.isDeleted = isDeleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContactName() {
        return ContactName;
    }

    public void setContactName(String contactName) {
        this.ContactName = contactName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String getPhone1() {
        return Phone1;
    }

    public void setPhone1(String phone1) {
        Phone1 = phone1;
    }

    public String getPhone2() {
        return Phone2;
    }

    public void setPhone2(String phone2) {
        Phone2 = phone2;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }
}
