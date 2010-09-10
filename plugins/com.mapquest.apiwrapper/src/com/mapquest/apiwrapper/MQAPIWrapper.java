package com.mapquest.apiwrapper;

import java.text.DecimalFormat;

import com.mapquest.DisplayState;
import com.mapquest.Exec;
import com.mapquest.LatLng;
import com.mapquest.MapState;
import com.mapquest.Session;

public class MQAPIWrapper {
    static final String MQ_MAP_SERVER_NAME          = "map.free.mapquest.com"; //$NON-NLS-1$
    static final String MQ_MAP_SERVER_PATH          = "mq"; //$NON-NLS-1$
    static final int    MQ_MAP_SERVER_PORT          =  80;
            
    private Exec mapClient;
           
    private static final DecimalFormat formatter = new DecimalFormat("##0.#################"); //$NON-NLS-1$
    
    public MQAPIWrapper() {
        mapClient = new Exec();
        
        mapClient.setServerName(MQ_MAP_SERVER_NAME);
        mapClient.setServerPath(MQ_MAP_SERVER_PATH);
        mapClient.setServerPort(MQ_MAP_SERVER_PORT);
        mapClient.setClientId("your-client-id"); //$NON-NLS-1$
        mapClient.setPassword("your-password"); //$NON-NLS-1$
    }
    
    public String getUrl(int scale, double x, double y, int width, int height) throws Exception {
        MapState mapState = new MapState();
        
        // build request
        mapState.setWidthPixels(width);
        mapState.setHeightPixels(height);
        
        mapState.setMapScale(scale);
        mapState.setCenter(new LatLng(y, x));
                    
        Session mqSession = new Session();            
        mqSession.addOne(mapState);
        
        // client-based call which creates the url
        return getCorrectUrl(
                mapClient.getMapDirectURLEx(mqSession, new DisplayState()),
                x,
                y);
    }
    
    /**
     * This is a work-around for a bug in the MQ API.
     * 
     * Coordinates values like 0.000030769 are converted to 3.0769 and
     * this method does correct that.
     * 
     * Typical Url:
     * http://map.free.mapquest.com/mq/mqserver.dll?e=0&GetMapDirect.1=Session:1,MapState:,nteur,51.498698,-0.12291,4.166666,1.388888,50000,DisplayState.1:0,72,1,Authentication.3:f126Bq%2B%7DnSA%3Acdc3,81371,,JAVA_5.3.0,-281739184,
     * 
     * @param mqUrl
     * @param x
     * @param y
     * @return
     */
    private String getCorrectUrl(String mqUrl, double x, double y) {
        try {
            int index3rdComma = mqUrl.indexOf(',', mqUrl.indexOf(',', mqUrl.indexOf(',') + 1) + 1);
            int index5thComma = mqUrl.indexOf(',', mqUrl.indexOf(',', index3rdComma + 1) + 1);
            
            return mqUrl.substring(0, index3rdComma + 1) +
                parseDouble(y) + "," + //$NON-NLS-1$
                parseDouble(x) + 
                mqUrl.substring(index5thComma, mqUrl.length()); 
            
        } catch (Exception exc) {}
                
        return mqUrl;
    }
    
    
    private String parseDouble(double value) {
        return formatter.format(value).replace(',', '.');
    }
}
