package me.zedaster.articleservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/articles/creators")
public class CreatorController {
    @PostMapping()
    public void changeUsername() {
        // TODO
    }
}
