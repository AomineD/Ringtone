package com.colvengames.downloadmp3.services;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadForShare extends AsyncTask<String, Integer, String>{


        private Context context;

        public DownloadForShare(Context context1){
           this.context = context1;
        }

        @Override
        protected String doInBackground(String... strings) {
            InputStream inputStream = null;
            OutputStream outputStream = null;

            HttpURLConnection connection = null;



            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                    return "Server returned HTTP "+connection.getResponseCode()+ " "+connection.getResponseMessage();
                }


                int tamaño = connection.getContentLength();


                inputStream = connection.getInputStream();
                outputStream = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/test.mp3");
byte data[] = new byte[4096];
long total = 0;
int count;

            while ((count = inputStream.read(data)) != -1){
                if(isCancelled()){
                    inputStream.close();
                    return null;
                }

                total += count;

                if(tamaño > 0)
                    publishProgress((int)(total * 100 / tamaño));
                outputStream.write(data, 0 , count);
            }



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if(outputStream != null)
                        outputStream.close();
                    if (inputStream != null)
                        inputStream.close();
                }catch (IOException ignored){

                }

                if(connection != null)
                    connection.disconnect();
            }

            return null;
        }

    @Override
    protected void onPostExecute(String result) {
        if (result != null)
            Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
    }


    }




