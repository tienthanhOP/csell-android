package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.constants.EntityAPI;

public class CustomerRetro implements Serializable{
    @SerializedName("custid")
    @Expose
    private String custId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("phone")
    @Expose
    private List<String> phone = null;
    @SerializedName("email")
    @Expose
    private List<String> email = null;
    @SerializedName("tags")
    @Expose
    private List<String> tags = null;
    @SerializedName("jobs")
    @Expose
    private String jobs;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("need")
    @Expose
    private String need;
    @SerializedName("groups")
    @Expose
    private List<String> groups = null;

    public Boolean isSelectedGroup = false;

    public Boolean isAdded = false;

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public List<String> getPhone() {
        return phone;
    }

    public void setPhone(List<String> phone) {
        this.phone = phone;
    }

    public List<String> getEmail() {
        return email;
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getJobs() {
        return jobs;
    }

    public void setJobs(String jobs) {
        this.jobs = jobs;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNeed() {
        return need;
    }

    public void setNeed(String need) {
        this.need = need;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public HashMap<String,Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("custid", custId);
        map.put("name", name);
        map.put("dob", dob);
        map.put("phone", phone);
        map.put("email", email);
        map.put("address", address);
        map.put("jobs", jobs);
        map.put("tags", tags);
        map.put("need", need);

        return map;
    }

    public LinkedTreeMap<String,Object> toMapGroup(){
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
        map.put("custid", custId);
        map.put(EntityAPI.FIELD_DISPLAY_NAME, name);

        return map;
    }

    public HashMap<String,Object> toMapContactLocal(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        if (phone != null){
            String[] phones = phone.toArray(new String[phone.size()]);
            map.put("phone", phones);
        }else {
            map.put("phone", null);
        }

        if (email != null){
            String[] emails = email.toArray(new String[email.size()]);
            map.put("email", emails);
        }else {
            map.put("email", null);
        }

        return map;
    }

    public CustomerRetro() {
    }

    public CustomerRetro(String name, List<String> phone) {
        this.name = name;
        this.phone = phone;
    }
}
