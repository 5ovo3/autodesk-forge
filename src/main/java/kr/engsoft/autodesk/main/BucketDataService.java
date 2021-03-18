package kr.engsoft.autodesk.main;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BucketDataService {

    private final BucketDataRepository mainRepository;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public BucketData getMain(String bucketKey) {
        return mainRepository.getMain(bucketKey);
    }

    public int insertBucketData(String bucketKey, String urn, String title) {
        BucketData bucketData = new BucketData();
        bucketData.setBucket_key(bucketKey);
        bucketData.setUrn(urn);
        bucketData.setTitle(title);
        bucketData.setCreate_time(sdf.format(new Date()));
        bucketData.setMain_YN("N");
        return mainRepository.insertBucketData(bucketData);
    }

    public int registerMain(String bucketKey, String urn, String title) {
        return mainRepository.registerMain(bucketKey, urn, title);
    }

    public List<BucketData> listBucketData(String bucketKey) {
        return mainRepository.listBucketData(bucketKey);
    }

    public int deleteBucketData(String bucketKey, String objectName) {
        return mainRepository.deleteBucketData(bucketKey, objectName);
    }

    public int insertWebhookId(String webhook_id, String bucketKey, String objectName) {
        return mainRepository.insertWebhookId(webhook_id, bucketKey, objectName);
    }

    public BucketData selectBucketData(String bucketKey, String objectName) {
        return mainRepository.selectBucketData(bucketKey, objectName);
    }

}
