package in.aviaryan.cfbuddy.ui;

import android.content.ContentValues;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.parceler.Parcels;

import in.aviaryan.cfbuddy.R;
import in.aviaryan.cfbuddy.data.Contract;
import in.aviaryan.cfbuddy.model.Problem;
import in.aviaryan.cfbuddy.parser.ProblemParser;
import in.aviaryan.cfbuddy.utils.Helper;
import in.aviaryan.cfbuddy.utils.VolleyErrorListener;


public class ProblemDetailActivity extends AppCompatActivity {

    Problem problem;
    TextView title;
    TextView id;
    WebView webView;
    RequestQueue queue;
    ProblemParser pdp;
    private final String TAG = "CFLOG_PDA";
    private static final String REAL_URL = "http://codeforces.com/problemset/problem/";
    private static final String URL = Contract.Cache.makeUIDFromRealUri(REAL_URL);  // 743/F

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // volley
        queue = Volley.newRequestQueue(this);
        // get parcel
        problem = (Problem) Parcels.unwrap(getIntent().getParcelableExtra("problem"));
        // init views
        title = (TextView) findViewById(R.id.pd_title);
        id = (TextView) findViewById(R.id.pd_id);
        webView = (WebView) findViewById(R.id.pd_webView);
        // load text
        webView.loadData(getString(R.string.text_loading), "text/html; charset=utf-8", "utf-8");
        webView.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        WebSettings ws = webView.getSettings();
        ws.setDefaultFontSize(11);
        // set texts
        title.setText(problem.name);
        id.setText(problem.contestId + problem.index);

        fetchBlog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
    }

    public void fetchBlog(){
        VolleyErrorListener vel = new VolleyErrorListener(this);
        pdp = new ProblemParser(this);
        // cache
        updateDisplayFromCache(Helper.getCache(getContentResolver(), URL + problem.contestId + problem.index));
        // request
        String url = REAL_URL + problem.contestId + "/" + problem.index;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, pdp, vel);
        queue.add(stringRequest);
    }

    public void updateDisplayFromCache(String cache){
        if (cache != null){
            updateDisplay(pdp.parse(cache));
        }
    }

    public void updateDisplay(String content) {
        Log.d(TAG, content.length() + "");
        problem.problemText = content;
        webView.loadData(content, "text/html; charset=utf-8", "utf-8");
    }

    public void updateCache(String data){
        Helper.updateCache(data, getContentResolver(), URL + problem.contestId + problem.index);
    }

    /*
     * Options Menu
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share){
            Helper.sharePlainText(this, REAL_URL + problem.contestId + "/" + problem.index);
        }
        return super.onOptionsItemSelected(item);
    }
}
