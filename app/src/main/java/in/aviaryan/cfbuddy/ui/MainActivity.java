package in.aviaryan.cfbuddy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import in.aviaryan.cfbuddy.R;
import in.aviaryan.cfbuddy.data.PrefUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    PrefUtils prefUtils;
    CircleImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefUtils = new PrefUtils(this);

        // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        // set handle in nav view
        ((TextView) headerView.findViewById(R.id.nav_handle)).setText(prefUtils.getHandle());
        // http://stackoverflow.com/questions/36867298/using-android-vector-drawables-on-pre-lollipop-crash
        VectorDrawableCompat vc = VectorDrawableCompat.create(getResources(), R.drawable.ic_account_circle_accent_24px, getTheme());
        userImage = (CircleImageView) headerView.findViewById(R.id.nav_image_view);
        userImage.setImageDrawable(vc);
        userImage.setDisableCircularTransformation(true);

        // display default
        displayFragment(new RecentBlogsFragment());
        navigationView.setCheckedItem(R.id.nav_manage);
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

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(this, UserActivity.class);
            intent.putExtra("handle", prefUtils.getHandle());
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {
            displayFragment(new ContestsFragment());
        } else if (id == R.id.nav_manage) {
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

    /*
     * Displays a fragment
     */
    private void displayFragment(Fragment fragment){
        if (fragment != null) {
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
}
