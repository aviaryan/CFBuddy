package in.aviaryan.cfbuddy.model;

import org.parceler.Parcel;

import java.util.Date;

@Parcel
public class Blog {
    public int id;
    public String handle;
    public String title;
    public String text;
    public Date time;

    // required by Parceler
    public Blog(){
    }
}
