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
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.InputStream;

/**
 * @author Béchet Léo, Nguyen Cyprien
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

    private String currentWebViewTitle = "Intro";
    private String Url = "https://en.wikipedia.org/wiki/Big_Buck_Bunny";
    private MetadataManager metadataManager;

    /**
     * Function that is called then this class is created
     * Create the links between the xml file and its components.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the layout from video_main.xml
        setContentView(R.layout.activity_main);

        //Remove the notification bar (full screen)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //MetadataManager
        metadataManager = new MetadataManager();
        InputStream is = getResources().openRawResource(R.raw.chapters);
        metadataManager.load(is);
        
        //Video
        // Find your VideoView in your video_main.xml layout
        myVideoView = findViewById(R.id.video_view);

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
            myVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bigbuckbunny));

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
        webview.loadUrl("https://en.wikipedia.org/wiki/Big_Buck_Bunny");

        //Button
        Button btnIntro = findViewById(R.id.btnIntro);
        Button btnTitle = findViewById(R.id.btnTitle);
        Button btnAssault = findViewById(R.id.btnAssault);
        Button btnButterflies = findViewById(R.id.btnButterflies);
        Button btnPayback = findViewById(R.id.btnPayback);
        Button btnCast = findViewById(R.id.btnCast);

        btnIntro.setTag("Intro");
        btnTitle.setTag("Title");
        btnAssault.setTag("Assault");
        btnButterflies.setTag("Butterflies");
        btnPayback.setTag("Payback");
        btnCast.setTag("Cast");

        btnIntro.setOnClickListener(myOnlyHandler);
        btnTitle.setOnClickListener(myOnlyHandler);
        btnAssault.setOnClickListener(myOnlyHandler);
        btnButterflies.setOnClickListener(myOnlyHandler);
        btnPayback.setOnClickListener(myOnlyHandler);
        btnCast.setOnClickListener(myOnlyHandler);


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
            //Le Bundle qui porte les données du Message et sera transmis au Handler
            Bundle messageBundle=new Bundle();
             //Le message échangé entre la Thread et le Handler
            Message myMessage;
            @Override
            public void run() {
                try {
                    while(true) {
                        if(myVideoView.isPlaying()) {

                            if(!currentWebViewTitle.equals(metadataManager.getContextByPosition(myVideoView.getCurrentPosition()/1000))){

                                currentWebViewTitle = metadataManager.getContextByPosition(myVideoView.getCurrentPosition()/1000);
                                // Envoyer le message au Handler (la méthode handler.obtainMessage est plus efficace
                                // que créer un message à partir de rien, optimisation du pool de message du Handler)
                                //Instanciation du message (la bonne méthode):
                                myMessage=mHandler.obtainMessage();
                                //Ajouter des données à transmettre au Handler via le Bundle
                                messageBundle.putString(PROGRESS_WEB_VIEW, metadataManager.getUrlByPosition(myVideoView.getCurrentPosition()/1000));
                                //Ajouter le Bundle au message
                                myMessage.setData(messageBundle);
                                //Envoyer le message
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
            metadataManager.show();
            //Just a test to get the tag (writing the tag directly in the xml file => getTag = null)
            Log.d(TAG,"Button on clicked, button tag: "+v.getTag());
            Log.d(TAG,"Button on clicked, video goes to : "+ metadataManager.getPositionByContext(v.getTag().toString()));
            Log.d(TAG,"Button on clicked, video currentDuration : "+myVideoView.getCurrentPosition());
            try{
                myVideoView.seekTo(metadataManager.getPositionByContext(v.getTag().toString())*1000);
            } catch (Exception e){
                Log.d(TAG, "Exception " + e);
            }

        }
    };

    /**
     * This function is usefull when we change the mobile orientation.
     * Changing the orientation (for example to landscape) will recreate the activity
     * Thus, the video will normaly restart from the biggening.
     * We save the current position of the video and seek with the onRestoreInstanceState function.
     *
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //Interrupt the current thread (or else, multiple messages are going to be sent to the handler)
        mThread.interrupt();
        //Save the current video position in order to restore
        savedInstanceState.putInt("Position", myVideoView.getCurrentPosition());
        //Pause the video (why not)
        myVideoView.pause();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt("Position");
        myVideoView.seekTo(position);
    }
}
