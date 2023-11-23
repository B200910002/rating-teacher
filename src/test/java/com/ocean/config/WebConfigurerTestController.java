package com.ocean.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebConfigurerTestController {

    @CrossOrigin(origins = "*")
    @GetMapping("/api/test-cors")
    public void testCorsOnApiPath() {}

    @CrossOrigin(origins = "*")
    @GetMapping("/test/test-cors")
    public void testCorsOnOtherPath() {}
}
