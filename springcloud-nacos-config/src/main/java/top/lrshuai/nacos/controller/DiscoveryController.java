package top.lrshuai.nacos.controller;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class DiscoveryController {

    @NacosInjected
    private NamingService namingService;

    @GetMapping("/discovery")
    public List<Instance> discovery(@RequestParam String serviceName) throws NacosException {
        System.out.println( "+++++++++ " + namingService + "===" + namingService.getServerStatus() );
        List<String> nacosservers = Arrays.asList( "127.0.0.1:8848" );

        List<Instance> svcinfo = namingService.getAllInstances(serviceName, nacosservers ,false );
        return namingService.getAllInstances(serviceName);
    }
}

