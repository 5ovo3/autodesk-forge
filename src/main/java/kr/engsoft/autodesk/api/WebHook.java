package kr.engsoft.autodesk.api;

import lombok.Getter;
import lombok.Setter;
import okhttp3.*;

import java.io.IOException;

@Getter
@Setter
public class WebHook {
    private String token;

    public WebHook(String token) {
        this.token = "Bearer " + token;
    }

    public String createDerivativeWebHook(String workflowId) {
        String callbackURL = "https://webhook.site/52fcafaf-1eb3-43a0-9539-fb0c3b5dee32";

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        String body = String.format("{\"callbackUrl\": \"%s\",\"scope\": { \"workflow\": \"%s\" }}", callbackURL, workflowId);
        RequestBody requestBody = RequestBody.create(body, mediaType);
        Request request = new Request.Builder()
                .url("https://developer.api.autodesk.com/webhooks/v1/systems/derivative/events/extraction.finished/hooks")
                .method("POST", requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("authorization", token)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String[] location = response.header("Location").split("/");

            return location[11];
        } catch (IOException e) {
            return "error";
        }
    }

    public String webhooks() {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url("https://developer.api.autodesk.com/webhooks/v1/hooks")
                .addHeader("Authorization", token)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "error";
        }
    }

    public String deleteWebhook(String webhook_id) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create("", mediaType);
        Request request = new Request.Builder()
                .url("https://developer.api.autodesk.com/webhooks/v1/systems/data/events/dm.version.added/hooks/" + webhook_id)
                .method("DELETE", body)
                .addHeader("Authorization", token)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "error";
        }
    }

}