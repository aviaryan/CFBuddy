package in.aviaryan.cfbuddy.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;


public class OpenUrlOnClickListener implements View.OnClickListener {

    private String url;
    private Context context;

    public OpenUrlOnClickListener(Context context, String url){
        this.url = url;
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }
}
