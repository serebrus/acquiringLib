package uz.ipakyulibank.acquiringlibrary;

/**
 * Created by adminn on 21.01.2016.
 */
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class PostResponseAsyncTask extends AsyncTask<String, Void, String> {

    private ProgressDialog progressDialog;

    private AsyncResponse delegate;
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private HashMap<String, String> postData =
            new HashMap<>();
    private String loadingMessage = "Загрузка...";
    private Boolean sw;

    //Constructor
    public PostResponseAsyncTask(AsyncResponse delegate){
        this.delegate = delegate;
        this.context = (Context)delegate;
    }

    PostResponseAsyncTask(AsyncResponse delegate,
                          HashMap<String, String> postData, Boolean sw) {

        this.delegate = delegate;
        this.context = (Context)delegate;
        this.postData = postData;
        this.sw = sw;
    }

    public PostResponseAsyncTask(AsyncResponse delegate, String loadingMessage) {
        this.delegate = delegate;
        this.context = (Context)delegate;
        this.loadingMessage = loadingMessage;
    }

    public PostResponseAsyncTask(AsyncResponse delegate,
                                 HashMap<String, String> postData, String loadingMessage) {

        this.delegate = delegate;
        this.context = (Context)delegate;
        this.postData = postData;
        this.loadingMessage = loadingMessage;
    }
    //End Constructor

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(loadingMessage);
        progressDialog.setCanceledOnTouchOutside(false);
        if (this.sw) {
            progressDialog.show();
        }
        super.onPreExecute();
    }//onPreExecute

    @Override
    protected String doInBackground(String... urls) {

        String result = "";

        for(int i = 0; i <= 0; i++) {

            result = invokePost(urls[i], postData);
        }

        return result;
    }//doInBackground

    private String invokePost(String requestURL, HashMap<String,
            String> postDataParams) {
        URL url;
        String response = "";
        try {
            /*
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            String algorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
            tmf.init(trustStore);

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
            */

            url = new URL(requestURL);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            //conn.setSSLSocketFactory(context.getSocketFactory());
            conn.setReadTimeout(120000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                //response="";

                Log.i("PostResponseAsyncTask", responseCode + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }//invokePost

    private String getPostDataString(HashMap<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for(Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }//getPostDataString

    @Override
    protected void onPostExecute(String result) {

        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }

        result = result.trim();

        delegate.processFinish(result);
    }//onPostExecute

    //Setter and Getter
    public String getLoadingMessage() {
        return loadingMessage;
    }

    public void setLoadingMessage(String loadingMessage) {
        this.loadingMessage = loadingMessage;
    }

    public HashMap<String, String> getPostData() {
        return postData;
    }

    public void setPostData(HashMap<String, String> postData) {
        this.postData = postData;
    }

    public Context getContext() {
        return context;
    }

    public AsyncResponse getDelegate() {
        return delegate;
    }

    //End Setter & Getter
}
