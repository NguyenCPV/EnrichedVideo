package fr.enssat.lnfl.enrichedvideo;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Béchet Léo, Nguyen Cyprien
 */

/**
 * The only one activity of the project.
 * When the application starts, this activity is created.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String PROGRESS_WEB_VIEW="Progress web view";

    private VideoView myVideoView;
    private WebView webview;

    private int position = 0;
    private ProgressDialog progressDialog;
    private MediaController mediaControls;

    private Handler mHandler;
    private Thread mThread;

    private JsonManager jsonManager;

    public String currentWebViewTitle = "Start";
    public String Url = "https://en.wikipedia.org/wiki/Big_Buck_Bunny";

    /**
     * Function that is called then this class is created
     * Create the links between the xml file and its components.
     * @param savedInstanceState save instance as bundle message
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the layout from activity_main.xml
        setContentView(R.layout.activity_landscape_only);

        //Remove the notification bar (full screen)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //JsonManager
        InputStream is = getResources().openRawResource(R.raw.chapters);
        jsonManager = new JsonManager(is);
        
        //Video
        myVideoView = findViewById(R.id.video_view);    // Find your VideoView in your video_main.xml layout
        if (mediaControls == null) {
            mediaControls = new MediaController(MainActivity.this);
        }
        // Create a progressbar
        progressDialog = new ProgressDialog(MainActivity.this);
        // Set progressbar title
        progressDialog.setTitle("JavaCodeGeeks Android Video View Example");
        // Set progressbar message
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        // Show progressbar
        progressDialog.show();

        try {
            myVideoView.setMediaController(mediaControls);
            myVideoView.setVideoURI(Uri.parse("https://archive.org/download/Route_66_-_an_American_badDream/Route_66_-_an_American_badDream.avi"));
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        myVideoView.requestFocus();
        myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                progressDialog.dismiss();
                myVideoView.seekTo(position);
                if (position == 0) {
                    myVideoView.start();
                } else {
                    myVideoView.pause();
                }
            }
        });

        //WebView
        webview = findViewById(R.id.web_view);
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl("https://wiki.creativecommons.org/wiki/Route_66_-_An_American_(bad)_Dream");


        //Button
        LinearLayout btnLinearLayout = findViewById(R.id.btnLinearLayout);
        ArrayList<String> titles = jsonManager.getAllTitle();
        for(String title:titles){
            Log.d("testing", title);
            Button btn = new Button(this);
            btn.setText(title);
            btn.setTag(title);
            btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            btn.setOnClickListener(myOnlyHandler);
            btnLinearLayout.addView(btn);
        }

        //Handler
        mHandler = new VideoWebHandler(this.webview);
    }

    /**
     * In this function, we work on the handler (thread + send message)
     * The handler will (normally) receive a bundle message (PROGRESS_WEB_VIEW, value)
     * The message will be treated in the handleMessage(msg) function
     */
    public void onStart(){
        Log.d(TAG,"OnStart called");
        super.onStart();

        mThread = new Thread(new Runnable() {
            //Bundle message to send to the Handler
            Bundle messageBundle=new Bundle();
             //The message (value) to store in the bundle and to give to the handler.
            Message myMessage;
            @Override
            public void run() {
                try {
                    while(true) {
                        if(myVideoView.isPlaying() && jsonManager != null) {
                            //We only send a message to the handler in order to change the webView
                            String newUrl = jsonManager.getUrlByPosition(myVideoView.getCurrentPosition()/1000);
                            if(newUrl != null && !currentWebViewTitle.equals(newUrl)){
                                currentWebViewTitle = newUrl;
                                // Message part
                                //Create new message (the message.optain() methode doesn't work here)
                                myMessage=new Message();
                                //Add the message value (the URL)
                                messageBundle.putString(PROGRESS_WEB_VIEW, newUrl);
                                //Set the message bundle as message
                                myMessage.setData(messageBundle);
                                //Send message
                                Log.d("SedingMessage", myMessage.toString());
                                mHandler.sendMessage(myMessage);
                            }
                        }
                        //Let other threads to work
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        mThread.start();

    }

    /**
     *  Only one onclickListner function was needed in this project.
     *  This funciton is called by all buttons of the project and will move the video to the corresponding position depending on the button's tag.
     *  A test of verifying the tag was needed
     */
    private View.OnClickListener myOnlyHandler = new View.OnClickListener() {
        public void onClick(View v) {

            Log.d("myOnlyHandler","     " + (jsonManager.getPosFromTitle(v.getTag().toString())*1000));
            try{
                myVideoView.seekTo(jsonManager.getPosFromTitle(v.getTag().toString())*1000);
            } catch (Exception e){
                Log.d(TAG, "Exception " + e);
            }
        }
    };

}
