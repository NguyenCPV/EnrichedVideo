package fr.enssat.lnfl.enrichedvideo;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.VideoView;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Remove the notification bar (full screen)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        String pathVideo="https://www.youtube.com/watch?v=p9vinmVUoy4";
        Uri uriVideo = Uri.parse(pathVideo);

        RelativeLayout rl = findViewById(R.id.rlID);

        VideoView videoView = findViewById(R.id.videoViewID);
        videoView.setVideoURI(uriVideo);

        //rl.addView(videoView);

    }
}
