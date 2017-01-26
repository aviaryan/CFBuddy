package in.aviaryan.cfbuddy.ui;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import org.parceler.Parcels;

import in.aviaryan.cfbuddy.R;
import in.aviaryan.cfbuddy.model.Blog;

public class BlogDetailActivity extends AppCompatActivity {

    TextView title;
    TextView author;
    TextView time;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detail);

        // parent activitiy
        // http://stackoverflow.com/questions/12276027/how-can-i-return-to-a-parent-activity-correctly
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get parcel
        Blog blog = (Blog) Parcels.unwrap(getIntent().getParcelableExtra("blog"));
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
    }
}
