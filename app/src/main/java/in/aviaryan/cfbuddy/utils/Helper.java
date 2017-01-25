package in.aviaryan.cfbuddy.utils;

import org.jsoup.Jsoup;


public class Helper {
    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }
}
