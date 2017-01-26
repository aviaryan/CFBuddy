package in.aviaryan.cfbuddy.ui;

import android.content.ContentValues;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.parceler.Parcels;

import in.aviaryan.cfbuddy.R;
import in.aviaryan.cfbuddy.data.Contract;
import in.aviaryan.cfbuddy.model.Blog;
import in.aviaryan.cfbuddy.parser.BlogParser;
import in.aviaryan.cfbuddy.utils.Helper;
import in.aviaryan.cfbuddy.utils.VolleyErrorListener;


public class BlogDetailActivity extends AppCompatActivity {

    TextView title;
    TextView author;
    TextView time;
    WebView webView;
    RequestQueue queue;
    private final String TAG = "CFLOG_BDA";
    // param
    private final String URL = Contract.Cache.makeUIDFromRealUri("codeforces.com/api/blogEntry.view?blogEntryId=");

    Blog blog;
    BlogParser bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detail);

        // parent activitiy
        // http://stackoverflow.com/questions/12276027/how-can-i-return-to-a-parent-activity-correctly
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // volley
        queue = Volley.newRequestQueue(this);
        // get parcel
        blog = (Blog) Parcels.unwrap(getIntent().getParcelableExtra("blog"));
        // get views
        title = (TextView) findViewById(R.id.bd_title);
        author = (TextView) findViewById(R.id.bd_author);
        time = (TextView) findViewById(R.id.bd_time);
        webView = (WebView) findViewById(R.id.bd_webView);
        // load text
        webView.loadData("Loading ...", "text/html; charset=utf-8", "utf-8");
        webView.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        WebSettings ws = webView.getSettings();
        ws.setDefaultFontSize(10);
        // load others
        title.setText(blog.title);
        author.setText(blog.handle);
        time.setText(blog.time + "");

        // fetch
        fetchBlog();
    }

    public void fetchBlog(){
        VolleyErrorListener vel = new VolleyErrorListener(this);
        bp = new BlogParser(this);
        // cache
        updateDisplayFromCache(Helper.getCache(getContentResolver(), URL + blog.id));
        // request
        String url = "http://codeforces.com/api/blogEntry.view?blogEntryId=" + blog.id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, bp, vel);
        queue.add(jsonObjectRequest);
    }

    public void updateDisplayFromCache(String cache){
        if (cache != null){
            updateDisplay(bp.parse(bp.stringToJson(cache)) );
        }
    }

    public void updateDisplay(Blog mBlog) {
        Log.d(TAG, mBlog.toString());
        blog = mBlog;
        webView.loadData(blog.text, "text/html; charset=utf-8", "utf-8");
    }

    public void updateCache(String data){
        Uri uri = Contract.Cache.URI;
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Cache.COLUMN_UID, URL + blog.id);  // make full url
        contentValues.put(Contract.Cache.COLUMN_DATA, data);
        contentValues.put(Contract.Cache.COLUMN_TIME, "");
        getContentResolver().insert(uri, contentValues);
    }
}
