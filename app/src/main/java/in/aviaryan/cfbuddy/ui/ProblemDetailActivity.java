package in.aviaryan.cfbuddy.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.parceler.Parcels;

import in.aviaryan.cfbuddy.R;
import in.aviaryan.cfbuddy.model.Problem;


public class ProblemDetailActivity extends AppCompatActivity {

    Problem problem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // get parcel
        problem = (Problem) Parcels.unwrap(getIntent().getParcelableExtra("problem"));
    }
}
