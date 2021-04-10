package com.example.trackie.ui.testmode;

import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.widget.Toast;

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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelPrediction {

    public List<String> topBSSIDs;
    public int size;

    private String CREDENTIALS_KEY = "{\n" +
            "  \"type\": \"service_account\",\n" +
            "  \"project_id\": \"trackiev2\",\n" +
            "  \"private_key_id\": \"4711d0f32270a09aff204ec472aa7eed6fb54ceb\",\n" +
            "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDZGsbuthqcuAzy\\nskzT/2ANSDm3dC5gGkP9or8dXcQKlh+mzmiBvSWrI5HubXZ31wqMxLLg9qsDZM+W\\nnrbnID4rA5PuHJ4e2928IKHlosj+oFBxNO/M68BaJj8qkknRuBjV7Q+UZBwxiTkR\\ndf9d5jvXiYZhQP6w688BxFcq0OX0MI9SdZ7rvRaxod2IRsk9OSxOOygDH9j+yvL3\\nMg57C/thuYp97tgRwxuCmMQWbgeev34GCY4K8K3c7kBG0/VEziK4+SF5Y7F7Zxlh\\n7bAjQbAtlvRxGof4+nqaZdIZwkVXCvPwbUFXzAyX8QzwPAVyXb3bsaElNexCbnQN\\nFJFO8QlxAgMBAAECggEAM2FI7zmdtrVAKzfTQuDPRJVxQ191kjzlFmOrwzFrse2x\\nc/KpfPrcrVHvuXOic94qmwH6K491DYqQLl9FfhF9ZQSlv9Z5/WefZGR8rDIIS2p5\\nXzFeBytMDIm8rUCNw9pD1m0SINZIbdjB7sX0N67DPtFHQkD1hb1o8kwbLQDfSxi7\\nWs3ZaptL2CS2K76NnHGncjoAfAFOO9rD5LdM3LSNLHm+FhDPbvhlZd3vdamlaA4P\\n/2TpbDMrQz0sYTKeOQAlB0okHvH0XoAIy0zIhv7WdnCQpGb05kpVNIYChtQGG7H4\\nb2SeSWk7TthUva531ioAmVBVPGd2M3oaDpCLkAckoQKBgQDuBki66JkJOqa2z2/+\\nyoFsnQ46fuwk92FJ8+kIielBhIW5dXdH2QuOwC2QQvk+MdyVQuM705QZ1WiUpPoA\\nroPV0N3bwCdimaBXGU4r8k27s9Hpca/up170hYuhFQk3GaY+c+EgtMcwKUoz4DPd\\nbNWEIO5TCaIu5FqgVUy0uOqbJQKBgQDpgAx4Y5ZLzHhjwHPeGy9Q+a06y4JU1tZN\\nrhjmcwsgDI7Z+3IoZ9wFWBn5PPlXWCoMBuf7pKayXBbK/IyN5XHH8qaWnZI2xsRL\\ndIjfqxIrQLW1QJYvVUBbFkjklZDdpv6BPZxwsBlqPv1o0z0dJou2SosUD/IbqhBN\\nc4mAedrpXQKBgQC97zXKMCEuRCcUaaMs5dsxGIJqGy3EscJoiMHDlokTRvSOcTWl\\nl4SpfKDfJRJOlus2EBHTg5oCo1C4jI0STi/VRr5CxKqo+33/AqpmVwvAZFV0ustw\\n3Q3ggVBSUb7wwP7vhvh2aMspm0ki7B1264nVzZFfoahRUtceafKzo3tSIQKBgQDm\\naxXjN2L5Ka0jwqaUZv8qSThI7MqQLfEMduDbiGtSFRJiyV69wL++BlKbQTBMoRPd\\nCnOiP7jACV/0r4OEAwdk6G678oJ2y26phzYKgiZLyG+HjVSnINmBCVylNXLfSAue\\nPc5C5YQu9DndV5bXTVJRcP4Nri6cXhX4WCs4PQdpgQKBgBdqCR+Oi1o+Hu4IqNYF\\nBqqokEOxIJRqe+6MGlTzi+ee1UMmKMtnNSWc7w7XpJs2aCATQlpE1Z63f8w8kdzg\\n7CXs1UzxboySbJPN0Ln6TLL+E1GjcYiXG/v5gFf9sTNe9fa257islvxIxyaNBrWU\\noh982BVaiM8uCIqWBf8H8Gdv\\n-----END PRIVATE KEY-----\\n\",\n" +
            "  \"client_email\": \"ml-manager@trackiev2.iam.gserviceaccount.com\",\n" +
            "  \"client_id\": \"108924010093481671203\",\n" +
            "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
            "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
            "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
            "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/ml-manager%40trackiev2.iam.gserviceaccount.com\"\n" +
            "}"; //ENTER CREDENTIALS KEY HERE

    // TODO: preprocess data coming in from WiFiScanner such that only RSSI from good BSSIDs are used
    private double[][] preprocessInputData(List<ScanResult> scanResults) {
        double[][] inputData = new double[1][size * 2];

        // get index from topBSSIDs, place RSSI in correct place
        for (ScanResult scanResult : scanResults) {
            if (topBSSIDs.contains(scanResult.BSSID)) {
                int index = topBSSIDs.indexOf(scanResult.BSSID);
                inputData[0][index] = 1;
                inputData[0][index + size] = (double) scanResult.level / -100;
            }
        }

        // for BSSIDs that are not found in scanResults, put -1 as RSSI
        for (int i = 0; i < size; i++) {
            if (inputData[0][i] == 0) {
                inputData[0][i + size] = -1;
            }
        }
        return inputData;
    }

    public ModelPrediction(List<String> topBSSIDs, int size) {
        this.topBSSIDs = topBSSIDs;
        this.size = size;
    }

    public void getPrediction(List<ScanResult> scanResults, OnReceivePredictionResultsCallback callback) {
        double [][] inputData = preprocessInputData(scanResults);
//        String inputJSON = createInputInstanceJSONFrom2DArray(inputData);
        // REMEMBER TO DELETE THE BOTTOM AND USE THE TOP
        String inputJSON = "{\"instances\": [[1.0," +
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
                "  0.8525]]}";
        SendPredictionThread thread = new SendPredictionThread(inputJSON, callback);
        thread.start();
    }

    private String createInputInstanceJSONFrom2DArray(double[][] inputArray) {
        Map<String, double[][]> map = new HashMap<>();
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

    public double[] testParseInputJSon() {
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
    }

    public double[] parsePredictionJSONForResult(String jsonResult) throws JSONException {
        JSONObject json = new JSONObject(jsonResult);
        JSONArray jsonArray = json.getJSONArray("predictions");
        double[] result = new double[]{jsonArray.getJSONArray(0).getDouble(0),
                                       jsonArray.getJSONArray(0).getDouble(1)};
        return result;
    }

    protected interface OnReceivePredictionResultsCallback {
        void onReceiveResults(double[] result);
        void onError();
    }
}
