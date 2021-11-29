package models;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Landlord")
public class Landlord extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_LANDLORDKEY = "landlordKey";
    public static final String KEY_POINTS = "points";

    public ParseUser getUser() { return getParseUser(KEY_USER); }

    public void setUser(ParseUser user) { put(KEY_USER, user); }

    public void setLandlordKey(String key){ put(KEY_LANDLORDKEY, key);}

    public String getLandLordKey(){return getString(KEY_LANDLORDKEY);}

    public Double getPoints() {return getDouble(KEY_POINTS);}

    public void setPoints(double points){put(KEY_POINTS, points);}

}
