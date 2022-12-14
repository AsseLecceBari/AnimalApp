package class_general;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetCoordinates  {
   String address;
   double lat,lng;

    public GetCoordinates(String address1) {
        String response;
        this.address=address1;

        Log.e("ciao12",address);
        try {

            //utilizzo l'oggetto httpdatahandler per effettuare la richiesta al server google
            HttpDataHandler http= new HttpDataHandler();
            //url che viene utilizzato per comunicare con l'api google ha address(che sarebbe l'indirizzo da geocodificare e chiave api
            String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=AIzaSyBcUs-OmuzIiP9WP_DShttueADR-GqvSwk",address);
            //metodo di httpdatahandler per effettuare la richiesta http
            response= http.getHTTPData(url);
            Log.e("ciao12",response);
            //creo un oggetto json dato che la richiesta http mi ritorna un file json contenente la geocodifica
            JSONObject jsonObject=new JSONObject(response);
            Log.e("ciao12",jsonObject.toString());
            //leggo il file json per ricavare latitudine e longitudine
            this.lat= (double) ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat");
            this.lng= (double) ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng");

        }catch (Exception e){

        }

    }





    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
