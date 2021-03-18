package kr.engsoft.autodesk.main;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "bucket_data")
public class BucketData {

    @Column(name = "bucket_key")
    private String bucket_key;

    @Id
    @Column(name = "urn")
    private String urn;

    @Column(name = "title")
    private String title;

    @Column(name = "create_time", updatable = false)
    private String create_time;

    @Column(name = "main_YN")
    private String main_YN;

    @Column(name = "webhook_id")
    private String webhook_id;
}
