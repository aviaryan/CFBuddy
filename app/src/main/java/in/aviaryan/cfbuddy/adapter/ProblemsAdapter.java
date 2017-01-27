package in.aviaryan.cfbuddy.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.ArrayList;

import in.aviaryan.cfbuddy.R;
import in.aviaryan.cfbuddy.model.Problem;
import in.aviaryan.cfbuddy.ui.BlogDetailActivity;
import in.aviaryan.cfbuddy.ui.ProblemDetailActivity;


public class ProblemsAdapter extends RecyclerView.Adapter<ProblemsAdapter.MyViewHolder> {

    private Context mContext;
    private final String TAG = "CFLOG_PBA";
    public ArrayList<Problem> problems;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView id;
        public TextView name;
        public TextView tags;
        public TextView solvedCount;
        public LinearLayout cardItem;

        public MyViewHolder(View view) {
            super(view);
            cardItem = (LinearLayout) view.findViewById(R.id.pb_card_item);
            name = (TextView) view.findViewById(R.id.pb_problem_title);
            tags = (TextView) view.findViewById(R.id.pb_problem_tags);
            solvedCount = (TextView) view.findViewById(R.id.pb_problem_count);
            id = (TextView) view.findViewById(R.id.pb_problem_id);
        }

    }

    public ProblemsAdapter(Context mContext, ArrayList<Problem> problems) {
        this.mContext = mContext;
        this.problems = problems;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_problem, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProblemsAdapter.MyViewHolder holder, int position) {
        final Problem problem = problems.get(position);
        holder.name.setText(problem.name);
        holder.solvedCount.setText("x" + problem.solvedCount);
        holder.id.setText(problem.contestId + problem.index);
        // tags
        String concat = "";
        for (String s: problem.tags){
            concat += s + ", ";
        }
        if (concat.length() > 0){
            concat = concat.substring(0, concat.length()-2);
        }
        holder.tags.setText(concat);
        // on click
        holder.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, problem.name);
                Intent intent = new Intent(mContext, ProblemDetailActivity.class);
                intent.putExtra("problem", Parcels.wrap(problem));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return problems.size();
    }
}
