package in.aviaryan.cfbuddy.model;

import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
public class Problem {
    public int contestId;
    public String index;
    public String name;
    public ArrayList<String> tags;
    public int solvedCount;
    public String problemText;

    // required by Parceler
    public Problem(){
    }
}
