package in.aviaryan.cfbuddy.parser;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import in.aviaryan.cfbuddy.ui.ProblemDetailActivity;


public class ProblemParser extends BaseParser implements Response.Listener<String> {
    private final String TAG = "CFLOG_PDP";
    AppCompatActivity activity;

    public ProblemParser(AppCompatActivity activity){
        this.activity = activity;
    }

    @Override
    public void onResponse(String response) {
//        Log.d(TAG, response);
        String result = parse(response);
        if (result != null){
            ((ProblemDetailActivity) activity).updateDisplay(result);
            ((ProblemDetailActivity) activity).updateCache(response);
        }
    }

    public String parse(String response){
        // http://howtodoinjava.com/jsoup/complete-jsoup-tutorial/
        Document document = Jsoup.parse(response);
        Element element = document.select("div[class=problem-statement]").first();
        Log.d(TAG, element.toString());
        return (element == null) ? response : element.html();
    }
}
