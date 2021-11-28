package models;
import android.util.Log;
import android.util.Pair;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.boltsinternal.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@ParseClassName("Tenant")
public class Tenant extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_LANDLORD = "landlord";
    public static final String KEY_POINTS = "points";

    public ParseUser getUser() { return getParseUser(KEY_USER); }

    public void setUser(ParseUser user) { put(KEY_USER, user); }

    public Landlord getLandlord() { return (Landlord) getParseObject(KEY_LANDLORD); }

    public void setLandlord(Landlord landlord) { put(KEY_LANDLORD, landlord); }

    public Double getPoints() {return getDouble(KEY_POINTS);}

    public void setPoints(double points){put(KEY_POINTS, points);}

}
