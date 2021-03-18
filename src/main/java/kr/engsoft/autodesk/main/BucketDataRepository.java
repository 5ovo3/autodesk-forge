package kr.engsoft.autodesk.main;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BucketDataRepository {
    private final EntityManager em;

    public BucketData getMain(String bucketKey) {
        String sql = "";

        sql += "SELECT bucket_key, urn, title, create_time, main_YN, webhook_id FROM bucket_data ";
        sql += "WHERE bucket_key = :bucket_key AND main_YN = 'Y' ";

        Query executeQuery = em.createNativeQuery(sql, BucketData.class);
        executeQuery.setParameter("bucket_key", bucketKey);

        BucketData bucketData = null;
        try {
            bucketData = (BucketData) executeQuery.getSingleResult();
        } catch (NoResultException nre) {
            System.out.println(nre);
        }
        return bucketData;
    }

    public int insertBucketData(BucketData bucketData) {
        String sql = "";

        sql += "INSERT INTO bucket_data ";
        sql += "(bucket_key, urn, title, create_time, main_YN) ";
        sql += "VALUES (?, ?, ?, ? ,?) ";

        Query executeQuery = em.createNativeQuery(sql, BucketData.class);
        executeQuery.setParameter(1, bucketData.getBucket_key());
        executeQuery.setParameter(2, bucketData.getUrn());
        executeQuery.setParameter(3, bucketData.getTitle());
        executeQuery.setParameter(4, bucketData.getCreate_time());
        executeQuery.setParameter(5, bucketData.getMain_YN());

        return executeQuery.executeUpdate();
    }

    public int registerMain(String bucketKey, String urn, String title) {
        String sql = "";

        sql += "UPDATE bucket_data SET main_YN = :main_YN ";
        sql += "WHERE bucket_key = :bucket_key AND urn = :urn AND title = :title ";

        Query executeQuery = em.createNativeQuery(sql, BucketData.class);
        executeQuery.setParameter("bucket_key", bucketKey);
        executeQuery.setParameter("urn", urn);
        executeQuery.setParameter("title", title);
        executeQuery.setParameter("main_YN", "Y");

        return executeQuery.executeUpdate();
    }

    public BucketData selectBucketData(String bucketKey, String objectName) {
        String sql = "";

        sql += "SELECT bucket_key, urn, title, create_time, main_YN, webhook_id FROM bucket_data ";
        sql += "WHERE bucket_key = :bucket_key AND title = :title ";

        Query executeQuery = em.createNativeQuery(sql, BucketData.class);
        executeQuery.setParameter("bucket_key", bucketKey);
        executeQuery.setParameter("title", objectName);

        return (BucketData) executeQuery.getSingleResult();
    }

    public List<BucketData> listBucketData(String bucketKey) {
        String sql = "";

        sql += "SELECT bucket_key, urn, title, create_time, main_YN, webhook_id FROM bucket_data ";
        sql += "WHERE bucket_key = :bucket_key ORDER BY main_YN DESC, create_time ";

        Query executeQuery = em.createNativeQuery(sql, BucketData.class);
        executeQuery.setParameter("bucket_key", bucketKey);

        return executeQuery.getResultList();
    }

    public int deleteBucketData(String bucketKey, String objectName) {
        String sql = "";

        sql += "DELETE FROM bucket_data WHERE bucket_key = :bucket_key ";
        if (objectName != null) {
            sql += "AND title = :title ";
        }

        Query executeQuery = em.createNativeQuery(sql, BucketData.class);
        executeQuery.setParameter("bucket_key", bucketKey);
        if (objectName != null) {
            executeQuery.setParameter("title", objectName);
        }

        return executeQuery.executeUpdate();
    }

    public int insertWebhookId(String webhook_id, String bucketKey, String objectName) {
        String sql = "";

        sql += "UPDATE bucket_data SET webhook_id = :webhook_id ";
        sql += "WHERE bucket_key = :bucket_key AND title = :title ";

        Query executeQuery = em.createNativeQuery(sql, BucketData.class);
        executeQuery.setParameter("webhook_id", webhook_id);
        executeQuery.setParameter("bucket_key", bucketKey);
        executeQuery.setParameter("title", objectName);

        return executeQuery.executeUpdate();
    }
}
