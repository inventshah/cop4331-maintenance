package models;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Handyman")
public class Handyman extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_LANDLORDS = "landlords";
    public static final String KEY_POINTS = "points";

    public ParseUser getUser() { return (ParseUser) get(KEY_USER); }

    public void setUser(ParseUser user) { put(KEY_USER, user); }

    public List<Landlord> getLandlords(){return getList(KEY_LANDLORDS);}

    public void setLandlords(List<Landlord> landlords){put(KEY_LANDLORDS, landlords);}

    public Double getPoints() {return getDouble(KEY_POINTS);}

    public void setPoints(double points){put(KEY_POINTS, points);}

}
