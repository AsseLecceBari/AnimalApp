package class_general;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpDataHandler {


    public HttpDataHandler() {
    }

    public String getHTTPData(String requestUrl){
        URL url;
        String response="";
        try {
            //creo l'oggetto urls con l'url che serve per fare la richiesta della traduzione dell'indirizzo all'api google
            //requestUrl non è altro che l'indirizzo url contenente l'address da geocodificare e la chiave api
            url=new URL(requestUrl);
            //viene avviata la connessione con il server con un metodo di tipo GET
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            //response code serve per ricevere il codice di risposta della richiesta 200 vuol dire OK
            int responseCode= conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK){
                //leggo il file che mi restituisce la richiesta http
                String line;
                BufferedReader br= new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line= br.readLine())!=null){
                    response+=line;
                }
            }else{
                response="";
            }

        }catch (ProtocolException e )
        {
            e.printStackTrace();
        }catch (MalformedURLException e){

        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String postHTTPData(String requestUrl){
        URL url;
        String response="";
        try {
            //creo l'oggetto urls con l'url che serve per fare la richiesta della traduzione dell'indirizzo all'api google
            //requestUrl non è altro che l'indirizzo url contenente l'address da geocodificare e la chiave api
            url=new URL(requestUrl);

            //viene avviata la connessione con il server con un metodo di tipo POST
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            //response code serve per ricevere il codice di risposta della richiesta 200 vuol dire OK
            int responseCode= conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK){
                //leggo il file che mi restituisce la richiesta http
                String line;
                BufferedReader br= new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line= br.readLine())!=null){
                    response+=line;
                }
            }else{
                response="";
            }

        }catch (ProtocolException e )
        {
            e.printStackTrace();
        }catch (MalformedURLException e){

        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
