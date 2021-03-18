package kr.engsoft.autodesk.api;

import lombok.Getter;
import lombok.Setter;
import okhttp3.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Getter
@Setter
public class DataManagement {
    private String token;
    private String workflowId;

    public DataManagement(String token) {
        this.token = "Bearer " + token;
    }

    public DataManagement(String token, String workflowId) {
        this.token = "Bearer " + token;
        this.workflowId = workflowId;
    }

    public String getBucketList() {
        final int BUCKET_SIZE = 20;

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url("https://developer.api.autodesk.com/oss/v2/buckets?limit=" + BUCKET_SIZE)
                .addHeader("Authorization", token)
                .build();

        String result = "";
        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
            response.close();
        } catch (IOException e) {
            result = "NO";
        }
        return result;
    }

    public String createBucket(String name) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        String body = String.format("{\"bucketKey\": \"%s\",\"policyKey\": \"persistent\"}", name);
        RequestBody requestBody = RequestBody.create(body, mediaType);
        Request request = new Request.Builder()
                .url("https://developer.api.autodesk.com/oss/v2/buckets")
                .method("POST", requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", token)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return "OK";
            } else {
                return response.body().string();
            }
        } catch (IOException e) {
            return "error";
        }
    }

    public String getAllObjects(String bucketKey) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url("https://developer.api.autodesk.com/oss/v2/buckets/" + bucketKey + "/objects")
                .addHeader("Authorization", token)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "error";
        }
    }

    public String uploadFile(String bucketKey, MultipartHttpServletRequest mpRequest) throws IOException {

        MultipartFile multiPartFile = mpRequest.getFile("file");
        File file = new File(multiPartFile.getOriginalFilename());
        String extension = multiPartFile.getOriginalFilename().substring(multiPartFile.getOriginalFilename().lastIndexOf(".") + 1);
        if (extension.equals("dat")) {
            multiPartFile.transferTo(file);
        }
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multiPartFile.getBytes());
        fos.close();

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("model/obj");
        RequestBody body = RequestBody.create(file, mediaType);
        Request request = new Request.Builder()
                .url("https://developer.api.autodesk.com/oss/v2/buckets/" + bucketKey + "/objects/" + file.getName())
                .method("PUT", body)
                .addHeader("Authorization", token)
                .addHeader("Content-Type", "model/obj")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                file.delete();
            }
            return response.body().string();
        } catch (IOException e) {
            return "error";
        }
    }

    public String translateObject(String urn) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        String body = "{\"input\": {\"urn\": \"" + urn + "\"},\"output\": {\"formats\": [{\"type\": \"svf\",\"views\": [\"3d\", \"2d\"]}],\"advanced\": {\"generateMasterViews\": true}}, \"misc\": { \"workflow\": \"" + workflowId + "\" }}";
        RequestBody requestBody = RequestBody.create(body, mediaType);
        Request request = new Request.Builder()
                .url("https://developer.api.autodesk.com/modelderivative/v2/designdata/job")
                .method("POST", requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", token)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "error";
        }
    }

    public String deleteObject(String bucketKey, String objectName, String uploadPath) {

        String extension = objectName.substring(objectName.lastIndexOf(".") + 1);

        if (extension.equals("dat")) {
            File file = new File(uploadPath + objectName);
            file.delete();
        }

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create("", mediaType);
        Request request = new Request.Builder()
                .url("https://developer.api.autodesk.com/oss/v2/buckets/" + bucketKey + "/objects/" + objectName)
                .method("DELETE", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", token)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "error";
        }
    }

    public String deleteBucket(String bucketKey) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create("", mediaType);
        Request request = new Request.Builder()
                .url("https://developer.api.autodesk.com/oss/v2/buckets/" + bucketKey)
                .method("DELETE", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", token)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "error";
        }
    }


    public String getGuid(String urn) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url("https://developer.api.autodesk.com/modelderivative/v2/designdata/" + urn + "/metadata")
                .method("GET", null)
                .addHeader("Authorization", token)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "error";
        }
    }

    public String getObjectId(String urn, String guid) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url("https://developer.api.autodesk.com/modelderivative/v2/designdata/" + urn + "/metadata/" + guid)
                .method("GET", null)
                .addHeader("Authorization", token)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "error";
        }
    }

    public String getProperties(String urn, String guid) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url("https://developer.api.autodesk.com/modelderivative/v2/designdata/" + urn + "/metadata/" + guid + "/properties")
                .method("GET", null)
                .addHeader("Authorization", token)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "error";
        }
    }

    public String getManifest(String urn) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url("https://developer.api.autodesk.com/modelderivative/v2/designdata/" + urn + "/manifest")
                .method("GET", null)
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
