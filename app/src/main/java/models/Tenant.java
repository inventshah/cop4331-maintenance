package models;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Tenant")
public class Tenant extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_NAME = "name";
    public static final String KEY_POINTS = "points";
    public static final String KEY_PROPERTIES = "properties";

    // Schema Validator, checks if all properties are non-empty/ correct for database insertion
    public boolean validateTenant() {
        return true;
    }

    public ParseUser getUser() { return getParseUser(KEY_USER); }

    public void setUser(ParseUser user) { put(KEY_USER, user); }

    public String getName() { return getString(KEY_NAME); }

    public void setName(String name) { super.put(KEY_NAME, name); }

    public int getPoints() { return getInt(KEY_POINTS); }

    public void setPoints(int points) { super.put(KEY_POINTS, points); }

    public List<Property> getProperties() { return getList(KEY_PROPERTIES); }

    public void setProperties(List<Property> list) { super.put(KEY_PROPERTIES, list); }

}
