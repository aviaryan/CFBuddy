package in.aviaryan.cfbuddy.parser;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.android.volley.Response;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import in.aviaryan.cfbuddy.model.Blog;
import in.aviaryan.cfbuddy.ui.RecentBlogsFragment;
import in.aviaryan.cfbuddy.utils.Helper;


public class RecentBlogsParser extends BaseParser implements Response.Listener<JSONObject> {
    private final String TAG = "CFLOG_RBP";
    private Fragment fragment;

    public RecentBlogsParser(Fragment fragment){
        this.fragment = fragment;
    }

    @Override
    public void onResponse(JSONObject response) {
        Log.d(TAG, response.toString());
        ArrayList<Blog> blogs = parse(response);
        FirebaseCrash.log("tada");  // TODO: remove this
        if (blogs != null) {
            ((RecentBlogsFragment) fragment).updateData(blogs);
            ((RecentBlogsFragment) fragment).updateCache(response.toString());
        }
    }

    public ArrayList<Blog> parse(JSONObject jsonObject){
        ArrayList<Blog> blogs = new ArrayList<>();

        try {
            JSONArray result = jsonObject.getJSONArray("result");
            HashSet<Integer> blogSet = new HashSet<>(); // for duplicates

            for (int i=0; i<result.length(); i++){
                JSONObject blogData = result.getJSONObject(i).getJSONObject("blogEntry");
                Blog blog = new Blog();
                blog.handle = blogData.getString("authorHandle");
                blog.id = blogData.getInt("id");
                blog.title = Helper.html2text(blogData.getString("title"));
                blog.text = null;
                Date date = new Date();
                date.setTime(blogData.getLong("creationTimeSeconds") * 1000);  // in millis
                blog.time = date;
                if (blogSet.contains(blog.id)){
                    continue;
                } else {
                    blogSet.add(blog.id);
                }

                blogs.add(blog);
            }
            return blogs;
        } catch (JSONException e){
            FirebaseCrash.report(e);
            Log.d(TAG, e.toString());
            return null;
        }
    }
}
