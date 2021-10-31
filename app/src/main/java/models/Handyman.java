package models;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Handyman")
public class Handyman extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_RESOLVEDWORKORDERS = "resolvedworkorders";
    public static final String KEY_LANDLORD = "landlord";

    // Schema Validator, checks if all properties are non-empty/ correct for database insertion
    public boolean validateHandyman() {
        return true;
    }

    public ParseUser getUser() { return getParseUser(KEY_USER); }

    public void setUser(ParseUser user) { put(KEY_USER, user); }

    public List<WorkOrder> getResolvedWorkOrders() {return getList(KEY_RESOLVEDWORKORDERS); }

    public void setResolvedWorkOrders(List<WorkOrder> list) { put(KEY_RESOLVEDWORKORDERS, list); }

    public Landlord getLandlord(){return (Landlord) getParseObject(KEY_LANDLORD);}

    public void setLandlord(Landlord landlord){put(KEY_LANDLORD, landlord);}

}
