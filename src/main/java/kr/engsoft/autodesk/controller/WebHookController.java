package kr.engsoft.autodesk.controller;

import kr.engsoft.autodesk.api.Authentication;
import kr.engsoft.autodesk.api.WebHook;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class WebHookController {

    @ResponseBody
    @GetMapping("/webhook/list")
    public String webhookList() {
        String scope = "data:read";

        Authentication authentication = new Authentication(scope);
        authentication.setLeggedToken();
        WebHook webHook = new WebHook(authentication.getAccessToken());

        return webHook.webhooks();
    }
}