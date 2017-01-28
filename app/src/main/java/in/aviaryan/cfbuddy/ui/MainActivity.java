package in.aviaryan.cfbuddy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;

import de.hdodenhof.circleimageview.CircleImageView;
import in.aviaryan.cfbuddy.R;
import in.aviaryan.cfbuddy.data.PrefUtils;
import in.aviaryan.cfbuddy.model.User;
import in.aviaryan.cfbuddy.parser.UserParser;
import in.aviaryan.cfbuddy.utils.Helper;
import in.aviaryan.cfbuddy.utils.VolleyErrorListener;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    PrefUtils prefUtils;
    CircleImageView userImage;
    UserParser userParser;
    private String userHandle;
    private static String lastState;
    RequestQueue queue;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefUtils = new PrefUtils(this);

        FirebaseMessaging.getInstance().subscribeToTopic("POTD");  // Problem of the day

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        headerView = navigationView.getHeaderView(0);
        // http://stackoverflow.com/questions/36867298/using-android-vector-drawables-on-pre-lollipop-crash
        VectorDrawableCompat vc = VectorDrawableCompat.create(getResources(), R.drawable.ic_account_circle_accent_24px, getTheme());
        userImage = (CircleImageView) headerView.findViewById(R.id.nav_image_view);
        userImage.setImageDrawable(vc);
        userImage.setDisableCircularTransformation(true);
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfile();
            }
        });

        loadLastFragment();
    }

    @Override
    protected void onResume() {
        // Lifecycle: http://www.javatpoint.com/android-life-cycle-of-activity
        super.onResume();
        // Load if settings changed
        // runs after onCreate
        userHandle = prefUtils.getHandle();
        ((TextView) headerView.findViewById(R.id.nav_handle)).setText(userHandle);
        // volley
        queue = Volley.newRequestQueue(this);
        fetchUser();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            openProfile();
            return true;
        } else if (id == R.id.nav_problems) {
            displayFragment(new ProblemsFragment());
        } else if (id == R.id.nav_contests) {
            displayFragment(new ContestsFragment());
        } else if (id == R.id.nav_blogs) {
            displayFragment(new RecentBlogsFragment());
        } else if (id == R.id.nav_find_user) {
            Intent intent = new Intent(this, UserActivity.class);
            intent.putExtra("handle", prefUtils.getHandle());
            intent.putExtra("searchEnabled", true);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_about) {
            displayFragment(new AboutFragment());

        }

        navigationView.setCheckedItem(id);
        closeDrawer();
        return true;
    }

    private void openProfile(){
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra("handle", userHandle);
        startActivity(intent);
    }

    /*
     * Loads last active fragment
     */
    private void loadLastFragment(){
        if (lastState == null || lastState.equals("rb")){
            displayFragment(new RecentBlogsFragment());
            navigationView.setCheckedItem(R.id.nav_blogs);
        } else if (lastState.equals("ab")){
            displayFragment(new AboutFragment());
            navigationView.setCheckedItem(R.id.nav_about);
        } else if (lastState.equals("cs")){
            displayFragment(new ContestsFragment());
            navigationView.setCheckedItem(R.id.nav_contests);
        } else if (lastState.equals("pb")){
            displayFragment(new ProblemsFragment());
            navigationView.setCheckedItem(R.id.nav_problems);
        }
    }

    /*
     * Displays a fragment
     */
    private void displayFragment(Fragment fragment){
        if (fragment != null) {
            // save last state
            if (fragment instanceof RecentBlogsFragment){
                lastState = "rb";
            } else if (fragment instanceof AboutFragment){
                lastState = "ab";
            } else if (fragment instanceof ContestsFragment){
                lastState = "cs";
            } else if (fragment instanceof ProblemsFragment){
                lastState = "pb";
            }
            // go
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
    }

    /*
     * Closes drawer
     */
    private void closeDrawer(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    /*
     * Profile Pic
     * Loading
     * TODO: on settings change, reflect in Nav
     */
    public void fetchUser(){
        VolleyErrorListener vel = new VolleyErrorListener(this);
        userParser = new UserParser(this);
        // cache
        String cacheUrl = "codeforces.com/api/user.info?handles=";
        updateDisplayFromCache(Helper.getCache(getContentResolver(), cacheUrl + userHandle));
        // request
        String url = "http://codeforces.com/api/user.info?handles=" + userHandle;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, userParser, vel);
        queue.add(jsonObjectRequest);
    }

    public void updateDisplayFromCache(String cache){
        if (cache != null){
            updateDisplay(userParser.parse(userParser.stringToJson(cache)));
        }
    }

    public void updateDisplay(User user){
        userImage.setDisableCircularTransformation(false);
        Glide.with(this).load(user.smallAvatar).into(userImage);
    }

    public void updateCache(String data){
        Helper.updateCache(data, getContentResolver(), "codeforces.com/api/user.info?handles=" + userHandle);
    }
}
