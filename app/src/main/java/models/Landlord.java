package models;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Landlord")
public class Landlord extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_ALLWORKORDERS = "workorders";
    public static final String KEY_LANDLORDKEY = "landlordKey";

    // Schema Validator, checks if all properties are non-empty/ correct for database insertion
    public boolean validateLandlord() {
        return true;
    }

    public ParseUser getUser() { return getParseUser(KEY_USER); }

    public void setUser(ParseUser user) { put(KEY_USER, user); }

    public List<WorkOrder> getAllWorkOrders() { return getList(KEY_ALLWORKORDERS); }

    public void setWorkOrders(List<WorkOrder> list) { put(KEY_ALLWORKORDERS, list); }

    public void setLandlordKey(String key){ put(KEY_LANDLORDKEY, key);}

    public String getLandLordKey(){return getString(KEY_LANDLORDKEY);}

}
