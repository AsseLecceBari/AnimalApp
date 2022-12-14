package class_general;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class GeolocationClass {
    double lat,lng;


    public GeolocationClass(double lat, double lng) {
        String response;
        try {

            //utilizzo l'oggetto httpdatahandler per effettuare la richiesta al server google
            HttpDataHandler http= new HttpDataHandler();
            //url che viene utilizzato per comunicare con l'api google ha address(che sarebbe l'indirizzo da geocodificare e chiave api
            String url = String.format("https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyAyD_Tp-Hk4bYmikXi9s-T3LttY6UyAocI");
            //metodo di httpdatahandler per effettuare la richiesta http
            response= http.postHTTPData(url);
            Log.e("ciaooooo21",response.toString());
            //creo un oggetto json dato che la richiesta http mi ritorna un file json contenente la geocodifica
            JSONObject jsonObject=new JSONObject(response);
            Log.e("ciaooooo21",jsonObject.toString());
            //entro dentro il file json in "location" dentro al quale o gli oggetti lat e lng
            JSONObject jDisp = jsonObject.getJSONObject("location");
            Log.e("jDisp", jDisp.toString());
            //prendo gli oggetti lat e lng e faccio il cast
            lat= (double) jDisp.get("lat");
            lng=(double) jDisp.get("lng");


        }catch (Exception e){

        }
        this.lat = lat;
        this.lng = lng;
    }


    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
