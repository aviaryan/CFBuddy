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
import org.w3c.dom.Text;

import java.util.ArrayList;

import in.aviaryan.cfbuddy.R;
import in.aviaryan.cfbuddy.model.Blog;
import in.aviaryan.cfbuddy.ui.BlogDetailActivity;

// Thanks
// http://www.androidhive.info/2016/05/android-working-with-card-view-and-recycler-view/


public class RecentBlogsAdapter extends RecyclerView.Adapter<RecentBlogsAdapter.MyViewHolder> {

    private Context mContext;
    private final String TAG = "CFLOG_RBA";
    public ArrayList<Blog> blogs;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView author;
        public TextView time;
        public LinearLayout cardItem;

        public MyViewHolder(View view){
            super(view);
            cardItem = (LinearLayout) view.findViewById(R.id.rb_card_item);
            title = (TextView) view.findViewById(R.id.rb_blog_title);
            author = (TextView) view.findViewById(R.id.rb_blog_author);
            time = (TextView) view.findViewById(R.id.rb_blog_time);
        }

    }

    public RecentBlogsAdapter(Context mContext, ArrayList<Blog> blogs) {
        this.mContext = mContext;
        this.blogs = blogs;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_recent_blogs, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Blog blog = blogs.get(position);
        holder.title.setText(blog.title);
        holder.author.setText(blog.handle);
        holder.time.setText(blog.time + "");
        holder.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, blog.title);
                Intent intent = new Intent(mContext, BlogDetailActivity.class);
                intent.putExtra("blog", Parcels.wrap(blog));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return blogs.size();
    }

}
