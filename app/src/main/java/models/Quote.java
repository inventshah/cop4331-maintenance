package models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Quote")
public class Quote extends ParseObject {

    public static final String KEY_HANDYMAN = "handyman";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_DATE = "date";

    public Handyman getHandyman(){ return (Handyman) get(KEY_HANDYMAN); }

    public void setHandyman(Handyman handyman) { put(KEY_HANDYMAN, handyman); }

    public double getAmount(){ return getDouble(KEY_AMOUNT); }

    public void setAmount(double amount){ put(KEY_AMOUNT, amount); }

    public String getDate() { return getString(KEY_DATE); }

    public void setDate(String date) { put(KEY_DATE, date); }

}
