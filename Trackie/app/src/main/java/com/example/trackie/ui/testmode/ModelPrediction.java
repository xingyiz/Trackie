package com.example.trackie.ui.testmode;

import android.net.wifi.ScanResult;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UriTemplate;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.discovery.Discovery;
import com.google.api.services.discovery.model.JsonSchema;
import com.google.api.services.discovery.model.RestDescription;
import com.google.api.services.discovery.model.RestMethod;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelPrediction {

    public ArrayList<String> topBSSIDs;

    // TODO: preprocess data coming in from WiFiScanner such that only RSSI from good BSSIDs are used
    private int[][] preprocessInputData(List<ScanResult> scanResults) {
        int len = topBSSIDs.size();
        int[][] inputdata = new int[len][1];
        for (ScanResult scanResult : scanResults) {
            if (topBSSIDs.contains(scanResult.BSSID)) {
            }
        }
        return inputdata;
    }

    public ModelPrediction(ArrayList<String> topBSSIDs) {
        this.topBSSIDs = topBSSIDs;
    }

    public String getPrediction(List<ScanResult> scanResults) throws Exception {
        int [][] inputData = preprocessInputData(scanResults);

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GsonFactory gsonFactory = GsonFactory.getDefaultInstance();
        Discovery discovery = new Discovery.Builder(httpTransport, gsonFactory, null).build();

        RestDescription api = discovery.apis().getRest("ml", "v1").execute();
        RestMethod method = api.getResources().get("projects").getMethods().get("predict");

        JsonSchema param = new JsonSchema();
        String projectId = "trackie-2e28a";
        // You should have already deployed a model and a version.
        // For reference, see https://cloud.google.com/ml-engine/docs/deploying-models.
        String modelId = "trackie_model";
        String versionId = "trackie_v1";
        param.set(
                "name", String.format("projects/%s/models/%s/versions/%s", projectId, modelId, versionId));

        GenericUrl url =
                new GenericUrl(UriTemplate.expand(api.getBaseUrl() + method.getPath(), param, true));
        System.out.println(url);

        String contentType = "application/json";
        HttpContent content = ByteArrayContent.fromString(contentType, createInputInstanceJSONFrom2DArray(inputData));
//        File requestBodyFile = new File("input.txt");
//        HttpContent content = new FileContent(contentType, requestBodyFile);
        System.out.println(content.getLength());

        List<String> scopes = new ArrayList<>();
        scopes.add("https://www.googleapis.com/auth/cloud-platform");

        GoogleCredentials credential = GoogleCredentials.getApplicationDefault().createScoped(scopes);
        HttpRequestFactory requestFactory =
                httpTransport.createRequestFactory(new HttpCredentialsAdapter(credential));
        HttpRequest request = requestFactory.buildRequest(method.getHttpMethod(), url, content);

        String response = request.execute().parseAsString();
        System.out.println(response);
        return response;
    }

    private String createInputInstanceJSONFrom2DArray(int[][] inputArray) {
        Map<String, int[][]> map = new HashMap<>();
        map.put("instances", inputArray);
        JSONObject json = new JSONObject(map);
        return json.toString();
    }

}
