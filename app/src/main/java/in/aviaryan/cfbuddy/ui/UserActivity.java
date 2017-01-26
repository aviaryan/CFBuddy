package in.aviaryan.cfbuddy.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import in.aviaryan.cfbuddy.R;
import in.aviaryan.cfbuddy.data.Contract;
import in.aviaryan.cfbuddy.model.User;
import in.aviaryan.cfbuddy.parser.UserParser;
import in.aviaryan.cfbuddy.utils.Helper;
import in.aviaryan.cfbuddy.utils.VolleyErrorListener;


public class UserActivity extends AppCompatActivity {

    Toolbar mToolbar;
    RequestQueue queue;
    private final String TAG = "CFLOG_UA";
    private final String URL = Contract.Cache.makeUIDFromRealUri("codeforces.com/api/user.info?handles=");
    TextView handle;
    TextView fullname;
    TextView rating;
    TextView maxRating;
    TextView organization;
    TextView country;
    ImageView avatar;

    User user;
    UserParser userParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        user = new User();
        Log.d(TAG, getIntent().getStringExtra("handle"));
        user.handle = getIntent().getStringExtra("handle");
        mToolbar = (Toolbar) findViewById(R.id.ua_toolbar);
        mToolbar.setTitle(user.handle);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // volley
        queue = Volley.newRequestQueue(this);

        // instantiate views
        handle = (TextView) findViewById(R.id.ua_handle);
        fullname = (TextView) findViewById(R.id.ua_fullname);
        rating = (TextView) findViewById(R.id.ua_rating);
        maxRating = (TextView) findViewById(R.id.ua_max_rating);
        organization = (TextView) findViewById(R.id.ua_org);
        country = (TextView) findViewById(R.id.ua_country);
        avatar = (ImageView) findViewById(R.id.ua_photo);

        // set text
        handle.setText(user.handle);

        fetchUser();
    }

    public void fetchUser(){
        VolleyErrorListener vel = new VolleyErrorListener(this);
        userParser = new UserParser(this);
        // cache
        updateDisplayFromCache(Helper.getCache(getContentResolver(), URL + user.handle));
        // request
        String url = "http://codeforces.com/api/user.info?handles=" + user.handle;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, userParser, vel);
        queue.add(jsonObjectRequest);
    }

    public void updateDisplayFromCache(String cache){
        if (cache != null){
            updateDisplay(userParser.parse(userParser.stringToJson(cache)) );
        }
    }

    @Override
    protected void onDestroy() {
        queue.stop();
        super.onDestroy();
    }

    public void updateDisplay(User mUser) {
        Log.d(TAG, mUser.toString());
        user = mUser;
        fullname.setText(user.name);
        rating.setText(user.rank + " (" + user.rating + ")");
        maxRating.setText(user.maxRank + " (" + user.maxRating + ")");
        organization.setText(user.organisation);
        country.setText(user.country);
        Glide.with(this).load(user.avatar)
                .centerCrop().placeholder(R.drawable.ic_menu_gallery)
                .crossFade().into(avatar);
    }

    public void updateCache(String data){
        Uri uri = Contract.Cache.URI;
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Cache.COLUMN_UID, URL + user.handle);  // make full url
        contentValues.put(Contract.Cache.COLUMN_DATA, data);
        contentValues.put(Contract.Cache.COLUMN_TIME, "");
        getContentResolver().insert(uri, contentValues);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Log.d(TAG, "what");

        if (id == R.id.ua_action_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_search_profile){

        }
        return super.onOptionsItemSelected(item);
    }
}
