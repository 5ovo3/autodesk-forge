package kr.engsoft.autodesk.controller;

import kr.engsoft.autodesk.api.Authentication;
import kr.engsoft.autodesk.main.BucketData;
import kr.engsoft.autodesk.main.BucketDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MenuController {
    private final BucketDataService bucketDataService;

    @GetMapping("/viewer")
    public String viewer(Model model, String urn) {

        Authentication authentication = new Authentication("data:read");
        authentication.setLeggedToken();
        model.addAttribute("token", authentication.getAccessToken());
        model.addAttribute("urn", "urn:" + urn);

        return "viewer";
    }

    @PostMapping("/viewer_all")
    public String viewer_all(Model model, String bucketKey) {

        List<BucketData> main_list = bucketDataService.listBucketData(bucketKey);

        Authentication authentication = new Authentication("data:read");
        authentication.setLeggedToken();
        model.addAttribute("token", authentication.getAccessToken());
        model.addAttribute("main_list", main_list);
        model.addAttribute("bucketKey", bucketKey);

        return "viewer_all";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }
}