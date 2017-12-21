package fr.enssat.lnfl.enrichedvideo;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebView;

/**
 * Created by Léo on 21/12/2017.
 */

public class VideoWebHandler extends Handler{
    private WebView webview;

    public VideoWebHandler(WebView webView){
        super();
        this.webview = webView;
    }

    @Override
    public void handleMessage(Message msg){
        String url=msg.getData().getString(MainActivity.PROGRESS_WEB_VIEW);
        Log.d("URL","Message received :" + url);
        this.webview.loadUrl(url);
    }
}
