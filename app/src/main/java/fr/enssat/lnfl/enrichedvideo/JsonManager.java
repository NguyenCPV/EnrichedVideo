package fr.enssat.lnfl.enrichedvideo;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Léo on 18/01/2018.
 */

public class JsonManager {

    private JSONObject jsonObj;

    private HashMap<String, String> Film;
    private ArrayList<HashMap<String, String>> Chapters;
    private ArrayList<HashMap<String, String>> Keywords;



    private ArrayList<HashMap<String, String>> Waypoints;

    public JsonManager(InputStream inputStream){
        //Read input steam (ckeck)
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

        //Store all datas from the json file
        try {
            readJsonAndSave(byteArrayOutputStream);
        } catch (Exception e){
            Log.d("JsonManager", e.toString());
        }
        this.show();
    }


    private void readJsonAndSave(ByteArrayOutputStream byteArrayOutputStream){
        this.Film = new HashMap<>();
        this.Chapters = new ArrayList<>();
        this.Keywords = new ArrayList<>();
        this.Waypoints = new ArrayList<>();

        List<String> subLabelsForFilm = Arrays.asList("file_url", "title", "synopsis_url");
        List<String> subLabelsForChapters = Arrays.asList("pos", "title");
        List<String> subLabelsForKeywords = Arrays.asList( "title","url");
        List<String> subLabelsForWaypoints = Arrays.asList("lat", "lng", "label", "timestamp");

        //Read Json file
        try {
            jsonObj = new JSONObject(byteArrayOutputStream.toString());
        } catch (JSONException je) {
            Log.d("JsonException", je.toString());
        }
        //Save Json data
        if (jsonObj != null) {
            try {
                //Film part
                //Get Json Object corresponding to the label "Film"
                JSONObject filmJsonObject = jsonObj.getJSONObject("Film");
                for(String subLabel: subLabelsForFilm){
                    this.Film.put(subLabel, filmJsonObject.get(subLabel).toString());
                }




                //Chapters part
                //Get all Json data corresponding to the label
                JSONArray aJsonData = jsonObj.getJSONArray("Chapters");
                Log.d("JSON Chapters:", aJsonData.toString());
                //Log.d("test", aJsonData.toString());
                //Log.d("test"," " + aJsonData.length());
                for (int i = 0; i < aJsonData.length(); i++) {
                    //Get one Json object that has multiple values correcponding to the sublabels
                    JSONObject jo = aJsonData.getJSONObject(i);
                    //Create a hashMap in order to store the couple ("subLabel", value)
                    HashMap<String, String> hashMap = new HashMap<>();

                    for(String subLabel: subLabelsForChapters){
                        hashMap.put(subLabel, jo.get(subLabel).toString());
                    }
                    this.Chapters.add(hashMap);
                }

                //Waypoints part
                //Get all Json data corresponding to the label
                aJsonData = jsonObj.getJSONArray("Waypoints");
                Log.d("JSON WayPoints:", aJsonData.toString());
                //Log.d("test", aJsonData.toString());
                //Log.d("test"," " + aJsonData.length());
                for (int i = 0; i < aJsonData.length(); i++) {
                    //Get one Json object that has multiple values correcponding to the sublabels
                    JSONObject jo = aJsonData.getJSONObject(i);


                    //Create a hashMap in order to store the couple ("subLabel", value)
                    HashMap<String, String> hashMap = new HashMap<>();

                    for(String subLabel: subLabelsForWaypoints){
                        hashMap.put(subLabel, jo.get(subLabel).toString());
                    }
                    this.Waypoints.add(hashMap);
                }

                //Keywords part
                //Get Json Object corresponding to the label "Keywords"
                JSONArray keywordsJsonArray = jsonObj.getJSONArray("Keywords");

                Log.d("JSON keywods:", keywordsJsonArray.toString());
                Log.d("JSON keywod[0]", keywordsJsonArray.get(0).toString());
                Log.d("JSON keywod[1]", keywordsJsonArray.get(1).toString());
                for (int i = 0; i < keywordsJsonArray.length(); i++) {
                    //Create a hashmap to store each data
                    HashMap<String, String> hashMap = new HashMap<>();
                    //Get one Json object that has multiple values correcponding to the sublabels
                    JSONObject jo = keywordsJsonArray.getJSONObject(i);
                    String pos = jo.get("pos").toString();
                    JSONArray dataArray = jo.getJSONArray("data");
                    for (int j = 0; j < dataArray.length(); j++) {
                        hashMap.put("pos", pos);
                        JSONObject aData = dataArray.getJSONObject(j);
                        Log.d("JSON ADATA:", aData.toString());
                        for (int k = 0; k < subLabelsForKeywords.size(); k++) {
                            hashMap.put(subLabelsForKeywords.get(k), aData.get(subLabelsForKeywords.get(k)).toString());
                        }
                    }
                    this.Keywords.add(hashMap);
                }
            } catch (JSONException je) {
                Log.d("JsonException", je.toString());
            }
        }
    }




    public int getPosFromTitle(String title){
        boolean found = false;
        int resPos = 0;
        int i = 0;
        while(!found && i<this.Chapters.size()){
            if(this.Chapters.get(i).get("title")==title){
                found = true;
                resPos = Integer.parseInt(this.Chapters.get(i).get("pos"));
            }
            i++;
        }
        return resPos;
    }

    public ArrayList<String> getAllTitle(){
        ArrayList<String> res = new ArrayList<>();
        for(HashMap<String, String> hm: this.Chapters){
            res.add(hm.get("title"));
        }
        return res;
    }

    public String getUrlByPosition(Integer pos){
        boolean found = false;
        String resUrl = "https://en.wikipedia.org/wiki/U.S._Route_66";
        int i = 1;
        while(!found && i<this.Keywords.size()){
            if(Integer.parseInt(this.Keywords.get(i).get("pos"))>pos){
                found = true;
                resUrl = this.Keywords.get(i-1).get("url");
            }
            i++;
        }
        return resUrl;
    }




    public void show(){
        Log.d("show() Film :",this.Film.toString());
        Log.d("show() Chapters :",this.Chapters.toString());
        Log.d("show() Keywords :",this.Keywords.toString());
        Log.d("show() Waypoints :",this.Waypoints.toString());

    }




    public ArrayList<HashMap<String, String>> getWaypoints() {
        return Waypoints;
    }

}
