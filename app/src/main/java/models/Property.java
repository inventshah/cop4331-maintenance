package models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

@ParseClassName("Property")
public class Property extends ParseObject {

    public static final String KEY_ADDRESS = "address";
    public static final String KEY_WORKORDERHISTORY = "workorderhistory";

    public String getAddress() { return getString(KEY_ADDRESS); }

    public void setAddress(String address) { put(KEY_ADDRESS, address); }

    public List<WorkOrder> getWorkOrderHistory() {return getList(KEY_WORKORDERHISTORY);}

    public void setWorkOrderHistory(List<WorkOrder> list) { put(KEY_WORKORDERHISTORY, list);}
}
