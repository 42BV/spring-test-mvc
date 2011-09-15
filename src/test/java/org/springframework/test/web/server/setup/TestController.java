package org.springframework.test.web.server.setup;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @RequestMapping("/form")
    public @ResponseBody String form(){
        return "hello";
    }



}
