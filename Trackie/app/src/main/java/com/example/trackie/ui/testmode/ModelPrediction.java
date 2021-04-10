package com.example.trackie.ui.testmode;

import android.content.Context;
import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.widget.Toast;

import com.example.trackie.R;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UriTemplate;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.discovery.Discovery;
import com.google.api.services.discovery.model.JsonSchema;
import com.google.api.services.discovery.model.RestDescription;
import com.google.api.services.discovery.model.RestMethod;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelPrediction {

    public List<String> topBSSIDs;
    public int size;

    private Context context;
    private String CREDENTIALS_KEY; //ENTER CREDENTIALS KEY HERE

    // TODO: preprocess data coming in from WiFiScanner such that only RSSI from good BSSIDs are used
    private List<List<Double>> preprocessInputData(List<ScanResult> scanResults) {
        List<Double> inputData = new ArrayList<>(size * 2);

        for (int i = 0; i < size*2; i++) {
            inputData.add(i, 1.0);
        }

        Toast.makeText(context, "Initialised: " + inputData, Toast.LENGTH_SHORT).show();

        // get index from topBSSIDs, place RSSI in correct place
        for (ScanResult scanResult : scanResults) {
            if (topBSSIDs.contains(scanResult.BSSID)) {
                int index = topBSSIDs.indexOf(scanResult.BSSID);
                inputData.set(index, 1.0);
                inputData.set(index + size, (double) scanResult.level / -100.0);
            }
        }

        Toast.makeText(context, "topbssids: " + inputData, Toast.LENGTH_SHORT).show();

        // for BSSIDs that are not found in scanResults, put -1 as RSSI
        for (int i = 0; i < size; i++) {
            if (inputData.get(i) == 0.0) {
                inputData.set(i + size, -1.0);
            }
        }

        Toast.makeText(context, "not found: " + inputData, Toast.LENGTH_SHORT).show();

        List<List<Double>> data = new ArrayList<>();
        data.add(inputData);
        return data;
    }

    public ModelPrediction(List<String> topBSSIDs, int size, String credentials, Context context) {
        this.topBSSIDs = topBSSIDs;
        this.size = size;
        this.CREDENTIALS_KEY = credentials;
        this.context = context;
    }

    public void getPrediction(List<ScanResult> scanResults, OnReceivePredictionResultsCallback callback) {
        List<List<Double>> inputData = preprocessInputData(scanResults);
        Toast.makeText(context, "preprocessed: " + inputData.toString(), Toast.LENGTH_SHORT).show();
//        String inputJSON = createInputInstanceJSONFrom2DArray(inputData);
        // REMEMBER TO DELETE THE BOTTOM AND USE THE TOP
        /*String inputJSON = "{\"instances\": [[1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  1.0," +
                "  0.6225," +
                "  0.6733333333333333," +
                "  0.68," +
                "  0.675," +
                "  0.76," +
                "  0.716," +
                "  0.744," +
                "  0.6125," +
                "  0.6125," +
                "  0.6125," +
                "  0.6125," +
                "  0.682," +
                "  0.702," +
                "  0.704," +
                "  0.715," +
                "  0.704," +
                "  0.856," +
                "  0.855," +
                "  0.858," +
                "  0.856," +
                "  0.806," +
                "  0.7225," +
                "  0.775," +
                "  0.81," +
                "  0.704," +
                "  0.8525," +
                "  0.76," +
                "  0.8066666666666668," +
                "  0.8533333333333333," +
                "  0.856," +
                "  0.865," +
                "  0.8575," +
                "  0.8625," +
                "  0.704," +
                "  0.725," +
                "  0.82," +
                "  0.8078," +
                "  0.8175," +
                "  0.8125," +
                "  0.8525]]}";*/
        SendPredictionThread thread = new SendPredictionThread(createInputInstanceJSONFrom2DArray(inputData), callback);
        Toast.makeText(context, createInputInstanceJSONFrom2DArray(inputData), Toast.LENGTH_SHORT).show();
        thread.start();
    }

    private String createInputInstanceJSONFrom2DArray(List<List<Double>> inputArray) {
        Map<String, List<List<Double>>> map = new HashMap<>();
        map.put("instances", inputArray);
        JSONObject json = new JSONObject(map);
        return json.toString();
    }

    private class SendPredictionThread extends Thread {
        private String inputJSON;
        private OnReceivePredictionResultsCallback callback;

        public SendPredictionThread(String inputJSON, OnReceivePredictionResultsCallback callback) {
            this.inputJSON = inputJSON;
            this.callback = callback;
        }

        @Override
        public void run() {
            HttpTransport httpTransport = null;
            httpTransport = new NetHttpTransport();
            GsonFactory gsonFactory = GsonFactory.getDefaultInstance();
            Discovery discovery = new Discovery.Builder(httpTransport, gsonFactory, null).build();
            RestDescription api = null;
            try {
                api = discovery.apis().getRest("ml", "v1").execute();
            } catch (IOException e) {
                System.out.println("IO Exception restdescription");
                e.printStackTrace();
            }
            RestMethod method = api.getResources().get("projects").getMethods().get("predict");

            JsonSchema param = new JsonSchema();
            String projectId = "trackiev2";
            // You should have already deployed a model and a version.
            // For reference, see https://cloud.google.com/ml-engine/docs/deploying-models.
            String modelId = "B2L2_RF";
            String versionId = "B2L2_RF";
            param.set(
                    "name", String.format("projects/%s/models/%s/versions/%s", projectId, modelId, versionId));

            GenericUrl url =
                    new GenericUrl(UriTemplate.expand(api.getBaseUrl() + method.getPath(), param, true));

            String contentType = "application/json";
            HttpContent content = ByteArrayContent.fromString(contentType, inputJSON);

            try {
                System.out.println("Content length: " + content.getLength());
//                System.out.println("Input JSON string: " + inputJSON);
            } catch (IOException e) {
                System.out.println("IO Exception: Couldnt get content length");
                e.printStackTrace();
            }

            List<String> scopes = new ArrayList<>();
            scopes.add("https://www.googleapis.com/auth/cloud-platform");

            GoogleCredentials credential = null;

            try {
                credential = GoogleCredentials.fromStream(
                        new ByteArrayInputStream(CREDENTIALS_KEY.getBytes(StandardCharsets.UTF_8)));
            } catch (IOException e) {
                System.out.println("IOException: googlecredentials");
                e.printStackTrace();
            }
            HttpRequestFactory requestFactory =
                    httpTransport.createRequestFactory(new HttpCredentialsAdapter(credential));
            HttpRequest request = null;
            try {
                request = requestFactory.buildRequest(method.getHttpMethod(), url, content);
            } catch (IOException e) {
                System.out.println("IOException: Build request failed");
                e.printStackTrace();
            }

            System.out.println("Request: " + request);
            String response = null;
            try {
                response = request.execute().parseAsString();
            } catch (IOException e) {
                System.out.println("IOException: fail to execute request");
                e.printStackTrace();
            }
            System.out.println("Response: " + response);

            try {
                callback.onReceiveResults(parsePredictionJSONForResult(response));
            } catch (JSONException e) {
                e.printStackTrace();
                callback.onError();
            } catch (NullPointerException e) {
                callback.onError();
            }
        }
    }

    /*public double[] testParseInputJSon() {
        try {
            JSONObject json = new JSONObject("\"instances\" : [[1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  1.0,\n" +
                    "  0.6225,\n" +
                    "  0.6733333333333333,\n" +
                    "  0.68,\n" +
                    "  0.675,\n" +
                    "  0.76,\n" +
                    "  0.716,\n" +
                    "  0.7440000000000001,\n" +
                    "  0.6125,\n" +
                    "  0.6125,\n" +
                    "  0.6125,\n" +
                    "  0.6125,\n" +
                    "  0.682,\n" +
                    "  0.7020000000000001,\n" +
                    "  0.7040000000000001,\n" +
                    "  0.715,\n" +
                    "  0.7040000000000001,\n" +
                    "  0.856,\n" +
                    "  0.855,\n" +
                    "  0.858,\n" +
                    "  0.856,\n" +
                    "  0.8059999999999999,\n" +
                    "  0.7225,\n" +
                    "  0.775,\n" +
                    "  0.81,\n" +
                    "  0.7040000000000001,\n" +
                    "  0.8525,\n" +
                    "  0.76,\n" +
                    "  0.8066666666666668,\n" +
                    "  0.8533333333333333,\n" +
                    "  0.856,\n" +
                    "  0.865,\n" +
                    "  0.8575,\n" +
                    "  0.8625,\n" +
                    "  0.7040000000000001,\n" +
                    "  0.725,\n" +
                    "  0.82,\n" +
                    "  0.8079999999999999,\n" +
                    "  0.8175,\n" +
                    "  0.8125,\n" +
                    "  0.8525]]}");
            JSONArray jsonArray = json.getJSONArray("instances");
            double[] result = new double[]{jsonArray.getJSONArray(0).getDouble(0),
                    jsonArray.getJSONArray(0).getDouble(1)};
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    public double[] parsePredictionJSONForResult(String jsonResult) throws JSONException {
        try {
            JSONObject json = new JSONObject(jsonResult);
            JSONArray jsonArray = json.getJSONArray("predictions");
            double[] result = new double[]{jsonArray.getJSONArray(0).getDouble(0),
                    jsonArray.getJSONArray(0).getDouble(1)};
            return result;
        } catch (Exception e) {
            JSONObject json = new JSONObject(jsonResult);
            String error = json.getString("error");
            Toast.makeText(context, "Prediction failed due to " + error, Toast.LENGTH_LONG).show();
            return new double[]{0.0, 0.0};
        }
    }

    protected interface OnReceivePredictionResultsCallback {
        void onReceiveResults(double[] result);
        void onError();
    }
}
