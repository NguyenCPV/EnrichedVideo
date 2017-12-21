package fr.enssat.lnfl.enrichedvideo;

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

/**
 * The objectif of the this class is to read a json file, to store metadata in a linkedList "lMeatadata"
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

    public void add(Metadata metadata){
        this.lMetadata.add(metadata);
    }

    /**
     * This function add one metadata information as Metadata object to the list lMetadata
     * @param _msPosition
     * @param _context
     * @param _url
     */
    public void add(int _msPosition,String _context, String _url){
        this.lMetadata.add(new Metadata(_msPosition,_context,_url));
    }

    /**
     * This function returns the position of the video by the context (for example "title", "intro"...)
     * If one context matches with one metadata object's context in the lMetadata list, we return the attribute "position" (int) of this object.
     * @param context
     * @return the position (int) of the video that corresponds to the context of the video
     */
    public int getPositionByContext(String context){
        for (Metadata m:this.lMetadata){
            if(m.getContext().equals(context)){
                return m.getSPosition();
            }
        }
        return -1;
    }

    /**
     * This function is used to get the context of the video that is linked with a position (int) given in parameter
     * @param position a position of the video (in second)
     * @return the context (string) of the video that corresponds to a position of the video
     */
    public String getContextByPosition(int position){
        Metadata resMetadata = this.lMetadata.get(0);
        for (Metadata m:this.lMetadata){
            if(resMetadata.getSPosition() < m.getSPosition() && position > m.getSPosition()){
                resMetadata = m;
            }
        }
        return resMetadata.getContext();
    }

    /**
     * This function is used to get the partial or the complete URL of a web site, that corresponds to the position given as parameter.
     * @param position
     * @return the URL (string) of a web site linked with the position of the video.
     */
    public String getUrlByPosition(int position){
        Metadata resMetadata = this.lMetadata.get(0);
        for (Metadata m:this.lMetadata){
            if(resMetadata.getSPosition() < m.getSPosition() && position > m.getSPosition()){
                resMetadata = m;
            }
        }
        return resMetadata.getUrl();
    }

    /**
     * Function that load the json file and store metadata in the list lMetadata by the function "add" of this class
     * @param inputStream
     */
    public void load(InputStream inputStream) {
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
