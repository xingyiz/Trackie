package com.example.trackie.ui.testmode;

import com.example.trackie.ui.Prefs;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UriTemplate;
import com.google.api.client.http.javanet.NetHttpTransport;
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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelPrediction {
    private String CREDENTIALS_KEY; // ENTER CREDENTIALS KEY HERE
    private String LEGAL_POINTS;
    private String modelType;
    private String modelName;
    private String modelVersion;

    public ModelPrediction(String credentials, String legal_points) {
        this.CREDENTIALS_KEY = credentials;
        this.LEGAL_POINTS = legal_points;

        // set classification as default
        this.modelType = "clf";
        this.modelName = "B2L2_XTC";
        this.modelVersion = "B2L2_XTC256";
    }


    public void setModel(String modelType) {
        this.modelType = modelType;
        if (modelType.equals("reg")) {
            this.modelName = "B2L2_XT";
            this.modelVersion = "B2L2_XT3";
        } else if (modelType.equals("clf")) {
            this.modelName = "B2L2_XTC";
            this.modelVersion = "B2L2_XTC256";
        }
    }

    public void getPrediction(List<List<Double>> inputData, OnReceivePredictionResultsCallback callback) {
        String inputJSON = createInputInstanceJSONFrom2DArray(inputData);
        SendPredictionThread thread = new SendPredictionThread(inputJSON, callback);
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
            param.set(
                    "name", String.format("projects/%s/models/%s/versions/%s", projectId, modelName, modelVersion));

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
                System.out.println("credential key: " + CREDENTIALS_KEY);
                credential = GoogleCredentials.fromStream(
                        new ByteArrayInputStream(CREDENTIALS_KEY.getBytes(StandardCharsets.UTF_8)));
            } catch (IOException e) {
                System.out.println("IOException: googlecredentials");
                e.printStackTrace();
            }
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory(new HttpCredentialsAdapter(credential));
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
                callback.onReceiveResults(parsePredictionJSONForResult(response, modelType));
            } catch (JSONException e) {
                e.printStackTrace();
                callback.onError();
            } catch (NullPointerException e) {
                callback.onError();
            }
        }
    }

    public double[] parsePredictionJSONForResult(String jsonResult, String type) throws JSONException {
        double[] result;
        switch (type) {
            case "reg":
                JSONObject json = new JSONObject(jsonResult);
                JSONArray jsonArray = json.getJSONArray("predictions");
                result = new double[]{jsonArray.getJSONArray(0).getDouble(0),
                        jsonArray.getJSONArray(0).getDouble(1)};
                break;

            case "clf":
                JSONObject json2 = new JSONObject(jsonResult);
                JSONArray jsonArray2 = json2.getJSONArray("predictions");
                int result2 = jsonArray2.getInt(0);
                System.out.println("result2: " + result2);

                System.out.println("LEGAL_POINTS MP; " + LEGAL_POINTS);
                JSONObject legalPoints = new JSONObject(LEGAL_POINTS);
                JSONArray legalPointsArray = legalPoints.getJSONArray("LEGAL_POINTS");
                result = new double[]{legalPointsArray.getJSONArray(result2).getDouble(0),
                        legalPointsArray.getJSONArray(result2).getDouble(1)};
                System.out.println("CLF: " + result[0] + ", " + result[1]);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        return result;
    }

    protected interface OnReceivePredictionResultsCallback {
        void onReceiveResults(double[] result);
        void onError();
    }
}
