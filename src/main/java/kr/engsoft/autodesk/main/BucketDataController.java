package kr.engsoft.autodesk.main;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BucketDataController {

    private final BucketDataService mainService;

    @ResponseBody
    @GetMapping("/get_main")
    public BucketData getMain(@RequestParam String bucketKey) {
        return mainService.getMain(bucketKey);
    }

    @ResponseBody
    @GetMapping("/list_bucket_data")
    public List<BucketData> getMainList(@RequestParam String bucketKey) {
        return mainService.listBucketData(bucketKey);
    }

    @ResponseBody
    @GetMapping("/insert_bucket_data")
    public int insertMain(@RequestParam String bucketKey, @RequestParam String urn, @RequestParam String title) {
        return mainService.insertBucketData(bucketKey, urn, title);
    }

    @ResponseBody
    @GetMapping("/register_main")
    public int registerMain(@RequestParam String bucketKey, @RequestParam String urn, @RequestParam String title) {
        return mainService.registerMain(bucketKey, urn, title);
    }
}
