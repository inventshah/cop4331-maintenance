package models;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Landlord")
public class Landlord extends ParseObject {

    public static final String KEY_USER = "user";
    public static String KEY_PROPERTIES = "properties";

    // Schema Validator, checks if all properties are non-empty/ correct for database insertion
    public boolean validateLandlord() {
        return true;
    }

    public ParseUser getUser() { return getParseUser(KEY_USER); }

    public void setUser(ParseUser user) { put(KEY_USER, user); }

    public List<Property> getProperties() { return getList(KEY_PROPERTIES); }

    public void setProperties(List<Property> list) { put(KEY_PROPERTIES, list); }

}
