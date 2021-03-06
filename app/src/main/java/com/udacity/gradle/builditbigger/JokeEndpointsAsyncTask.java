package com.udacity.gradle.builditbigger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.edu.builditbigger.backend.myApi.MyApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

/**
 * Created by edu on 28/10/2015.
 */
public class JokeEndpointsAsyncTask extends AsyncTask<Void, Void, String> {
    protected Activity mActivity;
    private MyApi myApiService = null;
    private OnJokeDownloadListener mJokeListener;
    private ProgressDialog progressDialog;

    public JokeEndpointsAsyncTask(Activity activity, OnJokeDownloadListener jokeListener) {
        super();
        this.mActivity = activity;
        this.mJokeListener = jokeListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Create & Start Progress Dialog
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setMessage("Loading joke...");
        progressDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        if (myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
//                        .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    // - 10.0.2.2 is localhost's IP address in Genymotion emulator
                    .setRootUrl("http://10.0.3.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            myApiService = builder.build();
        }

        try {
            return myApiService.getJoke().execute().getData();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {

        progressDialog.dismiss();

        mJokeListener.onJokeDownloaded(result);

    }
}
