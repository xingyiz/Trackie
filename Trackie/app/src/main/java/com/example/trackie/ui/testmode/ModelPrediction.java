package com.example.trackie.ui.testmode;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UriTemplate;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.discovery.Discovery;
import com.google.api.services.discovery.model.JsonSchema;
import com.google.api.services.discovery.model.RestDescription;
import com.google.api.services.discovery.model.RestMethod;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ModelPrediction {
    public static String getPrediction() throws Exception {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GsonFactory gsonFactory = GsonFactory.getDefaultInstance();
        Discovery discovery = new Discovery.Builder(httpTransport, gsonFactory, null).build();

        RestDescription api = discovery.apis().getRest("ml", "v1").execute();
        RestMethod method = api.getResources().get("projects").getMethods().get("predict");

        JsonSchema param = new JsonSchema();
        String projectId = "YOUR_PROJECT_ID";
        // You should have already deployed a model and a version.
        // For reference, see https://cloud.google.com/ml-engine/docs/deploying-models.
        String modelId = "YOUR_MODEL_ID";
        String versionId = "YOUR_VERSION_ID";
        param.set(
                "name", String.format("projects/%s/models/%s/versions/%s", projectId, modelId, versionId));

        GenericUrl url =
                new GenericUrl(UriTemplate.expand(api.getBaseUrl() + method.getPath(), param, true));
        System.out.println(url);

        String contentType = "application/json";
        File requestBodyFile = new File("input.txt");
        HttpContent content = new FileContent(contentType, requestBodyFile);
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
}
