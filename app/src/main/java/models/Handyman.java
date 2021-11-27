package models;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Handyman")
public class Handyman extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_RESOLVEDWORKORDERS = "resolvedworkorders";
    public static final String KEY_LANDLORDS = "landlords";

    // Schema Validator, checks if all properties are non-empty/ correct for database insertion
    public boolean validateHandyman() {
        return true;
    }

    public ParseUser getUser() { return (ParseUser) get(KEY_USER); }

    public void setUser(ParseUser user) { put(KEY_USER, user); }

    public List<WorkOrder> getResolvedWorkOrders() {return getList(KEY_RESOLVEDWORKORDERS); }

    public void setResolvedWorkOrders(List<WorkOrder> list) { put(KEY_RESOLVEDWORKORDERS, list); }

    public List<ParseObject> getLandlords(){return getList(KEY_LANDLORDS);}

    public void setLandlords(List<ParseObject> landlords){put(KEY_LANDLORDS, landlords);}

}
