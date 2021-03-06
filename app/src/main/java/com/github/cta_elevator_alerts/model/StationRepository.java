package com.github.cta_elevator_alerts.model;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository to interact with Room database and
 * pull data from external API (stations and alerts)
 *
 * @author Southport Developers (Sam Siner & Tyler Arndt)
 */

public class StationRepository {

    private final StationDao mStationDao;
    private final ExecutorService executor;
    private final MutableLiveData<Boolean> connectionStatusLD;
    private final MutableLiveData<String> updateAlertsTimeLD;
    private final MutableLiveData<Integer> stationCountLD;

    private static volatile StationRepository INSTANCE;

    public static StationRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (StationRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new StationRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    private StationRepository(Application application) {
        StationRoomDatabase db = StationRoomDatabase.getDatabase(application);
        mStationDao = db.stationDao();
        updateAlertsTimeLD = new MutableLiveData<>();
        connectionStatusLD = new MutableLiveData<>();
        stationCountLD = new MutableLiveData<>();
        executor = Executors.newFixedThreadPool(4);
    }

    public LiveData<List<Station>> mGetAllAlertStations() {
        return mStationDao.getAllAlertStations();
    }

    public LiveData<List<Station>> mGetAllFavorites() {
        return mStationDao.getAllFavorites();
    }

    public List<String> mGetStationAlertIDs(){
        final List<String> list2 = new ArrayList<>();

        Thread thread = new Thread() {
            public void run() {
                list2.addAll(mStationDao.getAllAlertStationIDs());
            }
        };
        thread.start();
        try{
            thread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        return list2;
    }

    public void setConnectionStatus(boolean b){
        connectionStatusLD.postValue(b);
    }

    public boolean[] mGetAllRoutes(String stationID){
        boolean[] b = new boolean[8];
        Thread thread = new Thread() {
            public void run() {
                b[0] = mStationDao.getRed(stationID);
                b[1] = mStationDao.getBlue(stationID);
                b[2] = mStationDao.getBrown(stationID);
                b[3] = mStationDao.getGreen(stationID);
                b[4] = mStationDao.getOrange(stationID);
                b[5] = mStationDao.getPink(stationID);
                b[6] = mStationDao.getPurple(stationID);
                b[7] = mStationDao.getYellow(stationID);
            }
        };
        thread.start();
        try{
            thread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        return b;
    }

    private String s;
    public String mGetStationName(String stationID){
        Thread thread = new Thread() {
            public void run() {
                s = mStationDao.getName(stationID);
            }
        };
        thread.start();
        try{
            thread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        return s;
    }

    private void insert(Station station) {
        Thread thread = new Thread() {
            public void run() {
                mStationDao.insert(station);
            }
        };
        thread.start();
        try{
            thread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private boolean hasElevator = false;
    public boolean mGetHasElevator(String stationID) {
        Thread thread = new Thread() {
            public void run() {
                hasElevator = mStationDao.getHasElevator(stationID);
            }
        };
        thread.start();
        try{
            thread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        return hasElevator;
    }

    private boolean hasElevatorAlert = false;
    public boolean mGetHasElevatorAlert(String stationID){
        Thread thread = new Thread() {
            public void run() {
                hasElevatorAlert = mStationDao.getHasElevatorAlert(stationID);
            }
        };
        thread.start();
        try{
            thread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        return hasElevatorAlert;
    }

//    private String convertDateTime(String s){
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH:mm:ss", Locale.US);
//        try {
//            Date originalDate = dateFormat.parse(s);
//            SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMMM' 'dd', 'yyyy' at 'h:mm a", Locale.US);
//            return dateFormat2.format(originalDate);
//        } catch (ParseException e) {
//            return s;
//        }
//    }

    public List<String> getAlertDetails(String stationID){
        final List<String> list2 = new ArrayList<>();

        Thread thread = new Thread() {
            public void run() {
                list2.add(0, mStationDao.getName(stationID));
                String shortDesc = mStationDao.getShortDescription(stationID);

                if (shortDesc != null) list2.add(1, mStationDao.getShortDescription(stationID));
                else list2.add(1, "");
            }
        };
        thread.start();
        try{
            thread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        return list2;
    }

    public void addFavorite(String stationID){
        executor.execute(() -> mStationDao.addFavorite(stationID));
    }

    public void removeFavorite(String stationID){
        Thread thread = new Thread() {
            public void run() {
                mStationDao.removeFavorite(stationID);
            }
        };
        thread.start();
        try{
            thread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private boolean isFavorite = false;
    public boolean isFavorite(String stationID){
        Thread thread = new Thread() {
            public void run() {
                isFavorite = mStationDao.isFavoriteStation(stationID);
            }
        };
        thread.start();
        try{
            thread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        return isFavorite;
    }

    public LiveData<Boolean> getConnectionStatus(){ return connectionStatusLD;}
    public LiveData<String> getUpdatedAlertsTime(){ return updateAlertsTimeLD;}

    public void buildStations(){
        if (mStationDao.getStationCount() > 0) return;

        String JSONString = pullJSONFromWebService("https://data.cityofchicago.org/resource/8pix-ypme.json");

        try {
            JSONArray arr = new JSONArray(JSONString);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = (JSONObject) arr.get(i);
                String mapID = obj.getString("map_id");
                boolean ada = Boolean.parseBoolean(obj.getString("ada"));

                //fix incorrect data for Quincy/Wells
                if(mapID.equals("40040")){ ada = true; }

                boolean red = Boolean.parseBoolean(obj.getString("red"));
                boolean blue = Boolean.parseBoolean(obj.getString("blue"));
                boolean brown = Boolean.parseBoolean(obj.getString("brn"));
                boolean green = Boolean.parseBoolean(obj.getString("g"));
                boolean orange = Boolean.parseBoolean(obj.getString("o"));
                boolean pink = Boolean.parseBoolean(obj.getString("pnk"));
                boolean purple = Boolean.parseBoolean(obj.getString("p")) || Boolean.parseBoolean(obj.getString("pexp"));
                boolean yellow = Boolean.parseBoolean(obj.getString("y"));

                Station station = mStationDao.getStation(mapID);

                if (station == null) {
                    Station newStation = new Station(mapID);
                    String stationName;
                    try {
                        stationName = obj.getString("station_name");
                    } catch (JSONException e) {
                        stationName = "";
                    }

                    //name length is too long for this station
                    if (mapID.equals("40850")){ stationName = "Harold Wash. Library"; }
                    if (mapID.equals("40670")){ stationName = "Western (O'Hare)"; }
                    if (mapID.equals("40220")){ stationName = "Western (Forest Pk)"; }
                    if (mapID.equals("40750")){ stationName = "Harlem (O'Hare)"; }
                    if (mapID.equals("40980")){ stationName = "Harlem (Forest Pk)"; }
                    if (mapID.equals("40810")){ stationName = "IL Med. District"; }
                    if (mapID.equals("41690")){ stationName = "Cermak-McCorm. Pl."; }

                    insert(newStation);
                    mStationDao.updateName(mapID, stationName);
                }

                //Set routes that come to this station
                if (ada) mStationDao.setHasElevator(mapID);
                if (red){ mStationDao.setRedTrue(mapID); }
                if (blue){ mStationDao.setBlueTrue(mapID); }
                if (brown){ mStationDao.setBrownTrue(mapID); }
                if (green){ mStationDao.setGreenTrue(mapID); }
                if (orange){ mStationDao.setOrangeTrue(mapID); }
                if (pink){ mStationDao.setPinkTrue(mapID); }
                if (purple){ mStationDao.setPurpleTrue(mapID); }
                if (yellow){ mStationDao.setYellowTrue(mapID); }
            }
            stationCountLD.postValue(mStationDao.getStationCount());
        } catch (JSONException e) {
            connectionStatusLD.postValue(false);
        }
    }

    public void buildAlerts(){
        String JSONString = pullJSONFromWebService("https://lapi.transitchicago.com/api/1.0/alerts.aspx?outputType=JSON");

        //Set internet connection status
        connectionStatusLD.postValue(!JSONString.equals("NO INTERNET"));
        if (JSONString.equals("NO INTERNET")) return;

        ArrayList<String> currentAlerts = new ArrayList<>(); //For multiple alerts
        ArrayList<String> beforeStationsOut = new ArrayList<>(mStationDao.getAllAlertStationIDs());

        try {
            JSONObject outer = new JSONObject(JSONString);
            JSONObject inner = outer.getJSONObject("CTAAlerts");
            JSONArray arrAlerts = inner.getJSONArray("Alert");

            for (int i=0;i<arrAlerts.length();i++){
                JSONObject alert = (JSONObject) arrAlerts.get(i);
                String impact = alert.getString("Impact");
                if (!impact.equals("Elevator Status")) continue;

                JSONArray service;
                try {
                    JSONObject impactedService = alert.getJSONObject("ImpactedService");
                    service = impactedService.getJSONArray("Service");
                } catch (JSONException e){
                    e.printStackTrace();
                    continue;
                }

                for (int j=0;j<service.length();j++) {
                    JSONObject serviceInstance = (JSONObject) service.get(j);
                    if (serviceInstance.getString("ServiceType").equals("T")) {
                        String id = serviceInstance.getString("ServiceId");
                        String headline = alert.getString("Headline");

                        if (headline.contains("Back in Service")) break;

                        //End up with beforeStationsOut only containing alerts that no longer exist
                        beforeStationsOut.remove(id);

                        //Looking for multiple alerts for the same station.
                        String shortDesc = alert.getString("ShortDescription");
                        if (currentAlerts.contains(id)) {
                            shortDesc += "\n\n";
                            shortDesc += mStationDao.getShortDescription(id);
                        } else {
                            currentAlerts.add(id);
                        }

                        mStationDao.setAlert(id, shortDesc);
                        break;
                    }
                }
            }
        }
        catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }

        for (String id : beforeStationsOut){
            mStationDao.removeAlert(id);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("'Last updated:\n'MMMM' 'dd', 'yyyy' at 'h:mm a", Locale.US);
        Date date = new Date(System.currentTimeMillis());
        updateAlertsTimeLD.postValue(dateFormat.format(date));
    }

    public void removeAlertKing(){
        Thread thread = new Thread() {
            public void run() {
                mStationDao.removeAlert("41140");
            }
        };
        thread.start();
        try{
            thread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void addAlertHoward(){
        Thread thread = new Thread() {
            public void run() {
                mStationDao.setAlert("40900", "Elevator is DOWN - TEST!");
            }
        };
        thread.start();
        try{
            thread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private String pullJSONFromWebService(String url){
        StringBuilder sb = new StringBuilder();
        try{
            URL urlStations = new URL(url);
            Scanner scan = new Scanner(urlStations.openStream());
            while (scan.hasNext()) sb.append(scan.nextLine());
            scan.close();
        } catch (IOException e) {
            sb.delete(0, sb.length());
            sb.append("NO INTERNET");
        }
        return sb.toString();
    }

    private List<String> lineAlertList;
    public List<String> getAllLineAlerts(String line){
        Thread thread = new Thread() {
            public void run() {
                switch(line){
                    case "Red Line":
                        lineAlertList = mStationDao.getAllRedLineAlertIDs();
                        Log.d("red line alerts: ", lineAlertList.toString());
                        break;
                    case "Blue Line":
                        lineAlertList = mStationDao.getAllBlueLineAlertIDs();
                        break;
                    case "Brown Line":
                        lineAlertList = mStationDao.getAllBrownLineAlertIDs();
                        break;
                    case "Green Line":
                        lineAlertList = mStationDao.getAllGreenLineAlertIDs();
                        break;
                    case "Orange Line":
                        lineAlertList = mStationDao.getAllOrangeLineAlertIDs();
                        break;
                    case "Pink Line":
                        lineAlertList = mStationDao.getAllPinkLineAlertIDs();
                        break;
                    case "Purple Line":
                        lineAlertList = mStationDao.getAllPurpleLineAlertIDs();
                        break;
                    case "Yellow Line":
                        lineAlertList = mStationDao.getAllYellowLineAlertIDs();
                        break;
                    default:
                        break;
                }
            }
        };
        thread.start();
        try{
            thread.join();
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        return lineAlertList;
    }
}