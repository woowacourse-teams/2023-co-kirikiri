package co.kirikiri.controller;

import co.kirikiri.common.interceptor.Authenticated;
import co.kirikiri.common.resolver.MemberIdentifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    @Authenticated
    public void test(@MemberIdentifier final String identifier) {
        // logic ...
    }
}
