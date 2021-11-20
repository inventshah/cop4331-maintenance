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
    public static final String KEY_SUBMITTEDWORKORDERS = "submittedWorkOrders";

    // // TODO finish this method
    // Schema Validator, checks if all properties are non-empty/ correct for database insertion
    public boolean validateTenant(String title, String description, String location, Tenant tenant,
                                  Landlord landlord) {
        return true;
    }

    public ParseUser getUser() { return getParseUser(KEY_USER); }

    public void setUser(ParseUser user) { put(KEY_USER, user); }

    public List<WorkOrder> getSubmittedWorkOrders(){ return getList(KEY_SUBMITTEDWORKORDERS);}

    public void setSubmittedWorkOrders(List<WorkOrder> list){ put(KEY_SUBMITTEDWORKORDERS, list);}

    // TODO finish this method
    public boolean createWorkOrder(String title, String description, String location, Tenant tenant,
                                    Landlord landlord, File photoFile) {

        // Don't set other properties yet, leave them undefined until set  workorder is resolved
        // or given a quote, etc

        // if(workorder is invalid)
        //    return false;
        WorkOrder workOrder = new WorkOrder();
        workOrder.setTitle(title);
        workOrder.setDescription(description);
        workOrder.setLocation(location);
        workOrder.setTenant(tenant);
        workOrder.setLandlord(landlord);
        workOrder.setStatus(false);
        workOrder.setAttachment(new ParseFile(photoFile));
        workOrder.setQuotes(new ArrayList<>());
        workOrder.setRating(0);
        workOrder.saveInBackground();

        return true;
    }
    public void removeWorkOrder(int index){
        // TODO: Run a query to delete, then save list
        // perhaps this will work, all of this in here is tentative
        List<WorkOrder> list = getSubmittedWorkOrders();
        WorkOrder workOrder = list.get(index);

        // remove reference from tenant
        list.remove(index);
        setSubmittedWorkOrders(list);

        // remove from server
        workOrder.deleteInBackground();
    }

    public Landlord getLandlord() { return (Landlord) getParseObject(KEY_LANDLORD); }

    public void setLandlord(Landlord landlord) { put(KEY_LANDLORD, landlord); }

}
