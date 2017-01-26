package in.aviaryan.cfbuddy.parser;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Response;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import in.aviaryan.cfbuddy.model.Blog;
import in.aviaryan.cfbuddy.ui.BlogDetailActivity;
import in.aviaryan.cfbuddy.utils.Helper;


public class BlogParser extends BaseParser implements Response.Listener<JSONObject> {
    private final String TAG = "CFLOG_BP";
    private AppCompatActivity activity;

    public BlogParser(AppCompatActivity activity){
        this.activity = activity;
    }

    @Override
    public void onResponse(JSONObject response) {
        Log.d(TAG, response.toString());
        Blog blog = parse(response);
        if (blog != null) {
            ((BlogDetailActivity) activity).updateDisplay(blog);
            ((BlogDetailActivity) activity).updateCache(response.toString());
        }
    }

    public Blog parse(JSONObject jsonObject){
        try {
            JSONObject blogData = jsonObject.getJSONObject("result");

            Blog blog = new Blog();
            blog.handle = blogData.getString("authorHandle");
            blog.id = blogData.getInt("id");
            blog.title = Helper.html2text(blogData.getString("title"));
            blog.text = blogData.getString("content");
            Date date = new Date();
            date.setTime(blogData.getLong("creationTimeSeconds") * 1000);  // in millis
            blog.time = date;
            return blog;
        } catch (JSONException e){
            FirebaseCrash.report(e);
            Log.d(TAG, e.toString());
            return null;
        }
    }
}
