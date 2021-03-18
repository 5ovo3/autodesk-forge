package kr.engsoft.autodesk.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import okhttp3.*;

import java.io.IOException;

@Getter
@Setter
public class Authentication {
    private static String CLIENT_ID = "y3efhGXKuNUONB6NSb7RWBs1g3RK97to";
    private static String CLIENT_SECRET = "4iodP0coWI1assYb";
    private static String GRANT_TYPE = "client_credentials";

    private String scope;
    private String accessToken;

    public Authentication(String scope) {
        this.scope = scope;
    }

    public void setLeggedToken() {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String body = String.format("client_id=%s&client_secret=%s&grant_type=%s&scope=%s", CLIENT_ID, CLIENT_SECRET, GRANT_TYPE, scope);
        RequestBody requestBody = RequestBody.create(body, mediaType);
        Request request = new Request.Builder().url("https://developer.api.autodesk.com/authentication/v1/authenticate")
                .method("POST", requestBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try {
            Response response = client.newCall(request).execute();
            JsonObject jsonObject = JsonParser.parseString(response.body().string()).getAsJsonObject();
            accessToken = jsonObject.get("access_token").getAsString();

            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
