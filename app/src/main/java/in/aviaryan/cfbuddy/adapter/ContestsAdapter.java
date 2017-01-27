package in.aviaryan.cfbuddy.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.aviaryan.cfbuddy.R;
import in.aviaryan.cfbuddy.model.Contest;
import in.aviaryan.cfbuddy.utils.Helper;
import in.aviaryan.cfbuddy.utils.OpenUrlOnClickListener;


public class ContestsAdapter extends RecyclerView.Adapter<ContestsAdapter.MyViewHolder> {

    private Context mContext;
    private final String TAG = "CFLOG_CSA";
    public ArrayList<Contest> contests;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView time;
        public TextView duration;
        public LinearLayout cardItem;

        public MyViewHolder(View view){
            super(view);
            cardItem = (LinearLayout) view.findViewById(R.id.cs_card_item);
            title = (TextView) view.findViewById(R.id.cs_contest_title);
            time = (TextView) view.findViewById(R.id.cs_contest_time);
            duration = (TextView) view.findViewById(R.id.cs_contest_duration);
        }

    }

    public ContestsAdapter(Context mContext, ArrayList<Contest> contests) {
        this.mContext = mContext;
        this.contests = contests;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_contest, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContestsAdapter.MyViewHolder holder, int position) {
        final Contest contest = contests.get(position);
        holder.title.setText(contest.name);
        holder.duration.setText(Helper.humanizeSecondsAccurate(contest.durationSeconds));
        holder.time.setText(Helper.dateToStr(contest.startTime));
        holder.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://codeforces.com/contest/" + contest.id;
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Contest url", url);
                clipboard.setPrimaryClip(clip);

                Snackbar
                    .make(view, R.string.contest_open_msg, Snackbar.LENGTH_LONG)
                    .setAction(R.string.contest_label_open,
                            new OpenUrlOnClickListener(mContext, url))
                    .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return contests.size();
    }
}
