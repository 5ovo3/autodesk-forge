package kr.engsoft.autodesk.controller;

import kr.engsoft.autodesk.api.Authentication;
import kr.engsoft.autodesk.api.DataManagement;
import kr.engsoft.autodesk.api.WebHook;
import kr.engsoft.autodesk.main.BucketData;
import kr.engsoft.autodesk.main.BucketDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class DataManagementController {
    private static String UPLOAD_PATH = "D:/upload/autodesk/";
    private final BucketDataService bucketDataService;

    @ResponseBody
    @GetMapping("/dm/bucket")
    public String bucketList() {
        String scope = "bucket:read";

        Authentication authentication = new Authentication(scope);
        authentication.setLeggedToken();
        DataManagement dataManagement = new DataManagement(authentication.getAccessToken());
        String bucketList = dataManagement.getBucketList();

        return bucketList;
    }


    @ResponseBody
    @PostMapping("/dm/bucket")
    public String bucketCreate(@RequestParam String name) {
        String scope = "bucket:create";

        Authentication authentication = new Authentication(scope);
        authentication.setLeggedToken();
        DataManagement dataManagement = new DataManagement(authentication.getAccessToken());

        return dataManagement.createBucket(name);
    }

    @ResponseBody
    @GetMapping("/dm/objects")
    public String objectList(@RequestParam String key) {
        String scope = "data:read";

        Authentication authentication = new Authentication(scope);
        authentication.setLeggedToken();
        DataManagement dataManagement = new DataManagement(authentication.getAccessToken());

        return dataManagement.getAllObjects(key);
    }

    @ResponseBody
    @PostMapping("/dm/objects/upload")
    public String objectFileUpload(MultipartHttpServletRequest mpRequest, @RequestParam String key) throws IOException {
        String scope = "data:write";

        Authentication authentication = new Authentication(scope);
        authentication.setLeggedToken();
        DataManagement dataManagement = new DataManagement(authentication.getAccessToken());

        return dataManagement.uploadFile(key, mpRequest);
    }

    @ResponseBody
    @GetMapping("/dm/objects/translate")
    public String objectTranslate(@RequestParam String urn, @RequestParam String bucketKey, @RequestParam String objectName) {
        String workflowId = UUID.randomUUID().toString();

        String scope = "data:read data:create";

        Authentication authentication = new Authentication(scope);
        authentication.setLeggedToken();
        WebHook webHook = new WebHook(authentication.getAccessToken());
        String webhook_id = webHook.createDerivativeWebHook(workflowId);
        bucketDataService.insertWebhookId(webhook_id, bucketKey, objectName);

        DataManagement dataManagement = new DataManagement(authentication.getAccessToken(), workflowId);

        return dataManagement.translateObject(urn);
    }

    @ResponseBody
    @GetMapping("/dm/objects/delete")
    public String objectDelete(@RequestParam String bucketKey, String objectName) {
        String scope = "data:write";

        Authentication authentication = new Authentication(scope);
        authentication.setLeggedToken();
        DataManagement dataManagement = new DataManagement(authentication.getAccessToken());

        WebHook webHook = new WebHook(authentication.getAccessToken());
        BucketData bucketData = bucketDataService.selectBucketData(bucketKey, objectName);
        if (null != bucketData.getWebhook_id()) {
            webHook.deleteWebhook(bucketData.getWebhook_id());
        }
        bucketDataService.deleteBucketData(bucketKey, objectName);

        return dataManagement.deleteObject(bucketKey, objectName, UPLOAD_PATH);
    }

    @ResponseBody
    @GetMapping("/dm/bucket/delete")
    public String bucketDelete(@RequestParam String bucketKey) {
        String scope = "bucket:delete data:read data:write";

        Authentication authentication = new Authentication(scope);
        authentication.setLeggedToken();
        DataManagement dataManagement = new DataManagement(authentication.getAccessToken());

        WebHook webHook = new WebHook(authentication.getAccessToken());
        List<BucketData> dataList = bucketDataService.listBucketData(bucketKey);
        for (int i = 0; i < dataList.size(); i++) {
            if (null != dataList.get(i).getWebhook_id()) {
                webHook.deleteWebhook(dataList.get(i).getWebhook_id());
            }
        }
        bucketDataService.deleteBucketData(bucketKey, null);

        return dataManagement.deleteBucket(bucketKey);
    }

    @ResponseBody
    @GetMapping("/dm/get_guid")
    public String getGuid(@RequestParam String urn) {
        String scope = "data:read";

        Authentication authentication = new Authentication(scope);
        authentication.setLeggedToken();
        DataManagement dataManagement = new DataManagement(authentication.getAccessToken());

        return dataManagement.getGuid(urn);
    }

    @ResponseBody
    @GetMapping("/dm/get_object_id")
    public String getObjectId(@RequestParam String urn, @RequestParam String guid) {
        String scope = "data:read";

        Authentication authentication = new Authentication(scope);
        authentication.setLeggedToken();
        DataManagement dataManagement = new DataManagement(authentication.getAccessToken());

        return dataManagement.getObjectId(urn, guid);
    }

    @ResponseBody
    @GetMapping("/dm/get_properties")
    public String getProperties(@RequestParam String urn, @RequestParam String guid) {
        String scope = "data:read";

        Authentication authentication = new Authentication(scope);
        authentication.setLeggedToken();
        DataManagement dataManagement = new DataManagement(authentication.getAccessToken());

        return dataManagement.getProperties(urn, guid);
    }

    @ResponseBody
    @GetMapping("/dm/get_manifest")
    public String getManifest(@RequestParam String urn) {
        String scope = "data:read";

        Authentication authentication = new Authentication(scope);
        authentication.setLeggedToken();
        DataManagement dataManagement = new DataManagement(authentication.getAccessToken());

        return dataManagement.getManifest(urn);
    }
}