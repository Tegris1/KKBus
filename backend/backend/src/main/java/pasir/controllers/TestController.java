package pasir.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pasir.AppInfo;

@RestController
@RequestMapping("/api")
public class TestController {
    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/info")
    public AppInfo info(){
        return new AppInfo(
                "Aplikacja Budżetowa",
                "1.0",
                "Witaj w aplikacji budżetowej stworzonej ze Spring Boot!");
    }
}
