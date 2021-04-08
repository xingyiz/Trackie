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
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.discovery.Discovery;
import com.google.api.services.discovery.model.JsonSchema;
import com.google.api.services.discovery.model.RestDescription;
import com.google.api.services.discovery.model.RestMethod;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelPrediction {

    public List<String> topBSSIDs;
    public int size;

    private final String CREDENTIALS_KEY = "{\n" +
            "  \"type\": \"service_account\",\n" +
            "  \"project_id\": \"trackie-2e28a\",\n" +
            "  \"private_key_id\": \"bb4a06f5a2de4f942f6cdb4c0d8320a94af5a625\",\n" +
            "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC4mnIXZO4LeWLF\\nOU9692Gv7GzAth6B/RpcRjvEzHms/jXRP596uCzzepBRIDnwzXrIsxFqhuDh9Bcg\\nHHbQU5IYDfhBHkrgFqaLWYwIaHC+U1nv0NOLmgmve4GOv8Q6Oi2VqPlDdJyfNvmO\\nXNAEJjWDBRSfCvUxuHc04bK2B1ezkQs9KESjSTS4V0dvTmcGA0if1MXkhYJMqD3v\\nh3X0k3EV6sqrVQnH+MDP0l1Nf61CtOE7Gd5oksUWgdvF+IhLTCpVfu87t85GvaS0\\nN7tDhuZ4Npm7kfgu+dKqYJAglwtsQk/eZKwS0MA7ZgsVxorU0MMo3UZwC72cZjrn\\nVP+XFNMNAgMBAAECggEACyEMhcqI61orETbOStyFICUBvXB9UFrg7vUw/0FQiJRe\\nzDmotK5Lz3XxI2Ls+053+4eIIkL1ZcngZwgwRYj0Qsz8zXg1DWNwMPB6F7usKF1A\\nC8wsJLtpoBGzEtcOzm/Zda9VL7LlClg3HVUNGWLwUPDKeHghphi33xFIQ98Xc3TN\\n2XgOXdl4vi8EBnv7zPtsIo61nlHwc0IrechhZBhKRgoC0uiD3A+rrz1vpaMtauib\\nq9pysy727LyvAWw7lKZmPVJn55OpxjKnt3N0RzKNhuXpJPV/O1t2ea5HKI1euLTh\\nNtGqk2jFv2ZXCuLbkWUGfGwm/iARaLdctrq19JBS4QKBgQDkPfZasDtGx6URE3BH\\nKDtBkyH9qUEKBF9FnJcYi5/2DfZRsI6/Re3HskUWg28QWG2cuzvYNOQy7kmlwSpv\\npMCKu8u/nwb4CiqpTOGUy32gOBjIEUs5UKcCxwRuvZX2i0jTRYRRhll5ne/cFB72\\nifKd5E+/jPvwd/xSNc23AR8NCQKBgQDPDdgvahdJQcTLarRxkszxu9iLpcCQkTPd\\nTt3CAPpTKV6+P9Kd7MnfNCg0pG5dq8AzK27uk3gB8hPQSSWh5CpgDkltjlWmO8TQ\\nX64HMjMVj6eh660sDik5r91ngSny222aKaIgS1NetQp3wVIm1unQ8S/O62qK7+F9\\nhMsUBTFa5QKBgArwYU9FZQHmbaXc3aO7bOOerqFerExTm1FVyPQXNQpw7t5JbP4D\\n1vqcM615jSgmZNJ/MsYQ+uLqAsOb93G0XroHeCNt9sKKkjLdCW69Nf8o0R89nPJZ\\nSDfc2/yGv5o6YLoneckCfGVUyXstx7IfOG6S07OliSmkRoaWJ47e0eMpAoGBAKTt\\nZzWlcTUGMWbC8UbCY4NuWLgr6K5/KuvuxU+ijcMuYexzVCY9ljO4DQqZq9vrUiaw\\nQyXAy8fo9wVjLpm88FAhMTYWOX03gLdmb24F+5ECI8oju4YrJYVw45Bt32c2iCBT\\nZ3QH7Dm1RchMck7Dw+2OIMHW3cAcAeaEn2qsFDtNAoGBANez86woF2QiNvzqcqy9\\n/qIzfSjFNVNNcyK2LpzJmdCjbkr91MbdIV2wlKU9eVeG1YuGF7B22iWOYss797/I\\nnDlXpEau583EJhwZrHPxmj3z19Dw4yoUJPpteYwMNTrqvlf1aE04dkT8wpW38VJl\\nzgmw+hXi2m7WawWXdHgs2Ytd\\n-----END PRIVATE KEY-----\\n\",\n" +
            "  \"client_email\": \"ai-engineer@trackie-2e28a.iam.gserviceaccount.com\",\n" +
            "  \"client_id\": \"104991642182561362583\",\n" +
            "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
            "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
            "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
            "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/ai-engineer%40trackie-2e28a.iam.gserviceaccount.com\"\n" +
            "}";

    // TODO: preprocess data coming in from WiFiScanner such that only RSSI from good BSSIDs are used
    private int[][] preprocessInputData(List<ScanResult> scanResults) {
        int[][] inputData = new int[1][size * 2];

        // get index from topBSSIDs, place RSSI in correct place
        for (ScanResult scanResult : scanResults) {
            if (topBSSIDs.contains(scanResult.BSSID)) {
                int index = topBSSIDs.indexOf(scanResult.BSSID);
                inputData[0][index] = 1;
                inputData[0][index + size] = scanResult.level / -100;
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

    public String getPrediction(List<ScanResult> scanResults) {
        int [][] inputData = preprocessInputData(scanResults);
        String inputJSON = createInputInstanceJSONFrom2DArray(inputData);

        System.out.println("Scan results: " + scanResults);

        SendPredictionThread thread = new SendPredictionThread(inputJSON);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String createInputInstanceJSONFrom2DArray(int[][] inputArray) {
        Map<String, int[][]> map = new HashMap<>();
        map.put("instances", inputArray);
        JSONObject json = new JSONObject(map);
        return json.toString();
    }

    private class SendPredictionThread extends Thread {
        private String inputJSON;
        private OnReceivePredictionResultsCallback callback;

        public SendPredictionThread(String inputJSON) {
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
            String projectId = "trackie-2e28a";
            // You should have already deployed a model and a version.
            // For reference, see https://cloud.google.com/ml-engine/docs/deploying-models.
            String modelId = "B2L2NEW_RF";
            String versionId = "B2L2NEW_RF";
            param.set(
                    "name", String.format("projects/%s/models/%s/versions/%s", projectId, modelId, versionId));

            GenericUrl url =
                    new GenericUrl(UriTemplate.expand(api.getBaseUrl() + method.getPath(), param, true));
            System.out.println(url);

            String contentType = "application/json";
            HttpContent content = ByteArrayContent.fromString(contentType, inputJSON);
//        File requestBodyFile = new File("input.txt");
//        HttpContent content = new FileContent(contentType, requestBodyFile);
            try {
                System.out.println("Content length: " + content.getLength());
                System.out.println("Input JSON string: " + inputJSON);
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
            System.out.println(response);
            callback.onReceiveResults(response);
        }
    }

    public interface OnReceivePredictionResultsCallback {
        void onReceiveResults(String result);
    }

    public PointF parsePredictionJSONForResult(String jsonResult) {
        // TODO: PARSE THIS SHIT
        // {
        //  "predictions": [
        //    [
        //      0.3058874895796178,
        //      0.42681561596691603
        //    ]
        //  ]
        //}
        return new PointF(0, 0);

    }

}
