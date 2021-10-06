package models;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("WorkOrder")
public class WorkOrder extends ParseObject {

    public static final String KEY_TENANT = "tenant";
    public static final String KEY_LANDLORD = "landlord";
    public static final String KEY_HANDYMAN = "handyman";
    public static final String KEY_PROPERTY = "property";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_QUOTE = "quote";
    public static final String KEY_RATING = "rating";
    public static final String KEY_STATUS = "status";
    public static final String KEY_ATTACHMENT = "attachment"; // image attachment

    // check if work order is valid, handyman can be null
    public boolean validateWorkOrder(){
        return true;
    }

    public Tenant getTenant() { return (Tenant)getParseObject(KEY_TENANT); }

    public void setTenant(Tenant tenant) { put(KEY_TENANT, tenant); };

    public Landlord getLandlord() { return (Landlord)getParseObject(KEY_LANDLORD); }

    public void setLandlord(Landlord landlord) { put(KEY_LANDLORD, landlord); }

    public Handyman getHandyman() { return (Handyman)getParseObject(KEY_HANDYMAN); }

    public void setHandyman(Handyman handyman) { put(KEY_HANDYMAN, handyman); }

    public Property getProperty() { return (Property)getParseObject(KEY_PROPERTY); }

    public void setProperty(Property property) { put(KEY_PROPERTY, property); }

    public String getTitle() { return getString(KEY_TITLE); }

    public void setTitle(String title) { put(KEY_TITLE, title); }

    public String getDescription() { return getString(KEY_DESCRIPTION); }

    public void setDescription(String description) { put(KEY_HANDYMAN, description); }

    public double getQuote() { return getDouble(KEY_QUOTE); }

    public void setQuote(double quote) { put(KEY_QUOTE, quote); }

    public int getRating() { return getInt(KEY_RATING); }

    public void setRating(int rating) { put(KEY_RATING, rating); }

    public boolean getStatus() { return getBoolean(KEY_STATUS); }

    public void setStatus(boolean status) { put(KEY_STATUS, status); }

    public ParseFile getAttachment() { return getParseFile(KEY_ATTACHMENT); }

    public void setAttachment(ParseFile attachment) { put(KEY_ATTACHMENT, attachment); }

}

