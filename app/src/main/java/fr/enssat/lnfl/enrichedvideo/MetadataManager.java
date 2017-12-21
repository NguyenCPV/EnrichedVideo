package fr.enssat.lnfl.enrichedvideo;

import android.provider.MediaStore;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Béchet Léo, Nguyen Cyprien
 */

public class MetadataManager {
    private List<Metadata> lMetadata;
    public MetadataManager(){
        this.lMetadata = new LinkedList<>();
        /*
        add(0,"Intro","");
        add(28,"Title","Production_history");
        add(2*60+40,"Assault","Release");
        add(4*60+50,"Payback","Plot");
        add(60+15,"Butterflies","Characters");
        add(8*60+15,"Cast","See_also");
        */
    }

    public void show ()
    {
        for (Metadata m:this.lMetadata){
            Log.d("BlaPos", " "+m.getSPosition());
            Log.d("Blacontext", " "+m.getContext());
            Log.d("Blaurl", " "+m.getUrl());

        }
    }

    public void add(Metadata metadata){
        this.lMetadata.add(metadata);
    }

    public void add(int _msPosition,String _context, String _url){
        this.lMetadata.add(new Metadata(_msPosition,_context,_url));
    }

    public int getPositionByContext(String context){
        for (Metadata m:this.lMetadata){
            if(m.getContext().equals(context)){
                return m.getSPosition();
            }
        }
        return -1;
    }

    public String getContextByPosition(int position){
        Metadata resMetadata = this.lMetadata.get(0);
        for (Metadata m:this.lMetadata){
            if(resMetadata.getSPosition() < m.getSPosition() && position > m.getSPosition()){
                resMetadata = m;
            }
        }
        return resMetadata.getContext();
    }

    public String getUrlByPosition(int position){
        Metadata resMetadata = this.lMetadata.get(0);
        for (Metadata m:this.lMetadata){
            if(resMetadata.getSPosition() < m.getSPosition() && position > m.getSPosition()){
                resMetadata = m;
            }
        }
        return resMetadata.getUrl();
    }

    public void load(InputStream inputStream) {
        Log.d("verify test","call");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("verify testest","mid");
        try {
            JSONObject jObject = new JSONObject(byteArrayOutputStream.toString());
            JSONArray jArray = jObject.getJSONArray("Chapters");
            int pos = 0;
            String url = "";
            String title = "";
            for (int i = 0; i < jArray.length(); i++) {
                pos = jArray.getJSONObject(i).getInt("pos");
                title = jArray.getJSONObject(i).getString("title");
                url = jArray.getJSONObject(i).getString("url");
                add(pos,title, url);
                Log.d("verify chapter",""+pos);
                Log.d("verify chapter",""+title);
                Log.d("verify chapter",""+url);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
