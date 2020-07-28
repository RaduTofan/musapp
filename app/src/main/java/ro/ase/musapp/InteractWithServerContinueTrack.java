package ro.ase.musapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class InteractWithServerContinueTrack extends AsyncTask<String, Void,String> {

    private String result = null;
    private Context context;


    public InteractWithServerContinueTrack(Context ctx){
        context=ctx;
    }


    @Override
    protected String doInBackground(String... strings) {


        HttpURLConnection httpURLConnection=null;


        try {
            httpURLConnection = (HttpURLConnection) new URL(strings[0]).openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);


            Log.d("JSON:",strings[1]);

            OutputStream out = new BufferedOutputStream(httpURLConnection.getOutputStream());

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(strings[1]);
            writer.flush();
            writer.close();
            out.close();


            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                result+="OK";
                File f = new File("/data/data/ro.ase.musapp/files/songscontinued.zip");
                InputStream initialStream = httpURLConnection.getInputStream();
                OutputStream outStream = new FileOutputStream(f);

                byte[] buffer = new byte[8096];
                int bytesRead;
                while ((bytesRead = initialStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);

                }

                initialStream.close();
                outStream.close();


            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if(result!=null) {
            Intent it = new Intent(context, ListViewWithContinuedResults.class);
            context.startActivity(it);
            FormularContinueActivity.FormularContinueActivity.finish();
        }else{
            Intent it = new Intent(context, FormularActivity.class);
            context.startActivity(it);
            Toast.makeText(context, "Failed to connect to the server", Toast.LENGTH_LONG).show();
            FormularContinueActivity.FormularContinueActivity.finish();


        }

    }


}