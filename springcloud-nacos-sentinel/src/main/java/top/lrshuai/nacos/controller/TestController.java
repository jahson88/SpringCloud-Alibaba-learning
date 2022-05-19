package top.lrshuai.nacos.controller;

import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.lrshuai.nacos.service.TestService;

@RequestMapping("/sentinel")
@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("/sayHi")
    public String sayHi(String name){
        return testService.sayHi(name);
    }

    @GetMapping("/remoteSayHi")
    public String remoteSayHi(String name){
        return testService.remoteSayHi(name);
    }

    @GetMapping("/remoteSayHiper")
    public String remoteSayHiper(String name){
        return testService.remoteSayHiper(name);
    }

    @GetMapping("/remoteSayHiDisc")
    public String remoteSayHiDisc(String name) throws NacosException {
        return testService.remoteSayHiDisc(name);
    }
}
