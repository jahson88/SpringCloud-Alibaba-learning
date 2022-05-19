package top.lrshuai.nacos.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.List;

@Service
public class TestService {

    @SentinelResource(value = "TestService#sayHi", defaultFallback = "bonjourFallback")
    public String sayHi(String name){
        return "Hi "+name;
    }

    @Autowired
    RestTemplate restTemplate;

    @NacosInjected
    private NamingService namingService;

    @SentinelResource(value = "TestService#remoteSayHi", defaultFallback = "bonjourFallback")
    public String remoteSayHi(String name){
        String result = restTemplate.getForObject("http://nacos-provider/test/sayHi?name="+name, String.class);
        return "访问provider 返回 : " + result;
    }



    /*
    nacos-provider 并没有自动寻址
    org.springframework.web.client.RestTemplate 不能调用nacos寻址
     */
    @SentinelResource(value = "TestService#remoteSayHiper", defaultFallback = "bonjourFallback")
    public String remoteSayHiper(String name){
        String result = restTemplate.getForObject("http://nacos-provider/test/sayHi?name={}&authority={}", String.class, new Object[]{ name, "token" });
        return "访问provider 返回 : " + result;
    }

    @SentinelResource(value = "TestService#remoteSayHiDisc", defaultFallback = "bonjourFallback")
    public String remoteSayHiDisc(String name) throws NacosException{
        Instance e = getInstance( "nacos-provider" );
        String result = restTemplate.getForObject("http://{host}:{port}/test/sayHi?name={name}&authority={token}", String.class, new Object[]{ e.getIp() , e.getPort(), name, "token"});
        return "访问provider 返回 : " + result;
    }

    public String bonjourFallback(Throwable t) {
        t.printStackTrace();
        if (BlockException.isBlockException(t)) {
            return "===Blocked by Sentinel: " + t.getClass().getSimpleName();
        }
        return "===Oops, failed: " + t.getClass().getCanonicalName();
    }


    private Instance getInstance( String svrname ) throws NacosException {
        List<Instance> ins = null;
        System.out.println( "+++++++++ " + namingService + "===" + namingService.getServerStatus() );
        ins = namingService.getAllInstances( "nacos-provider" , "DEFAULT_GROUP" );
        if( ins.size() >0 ){
            Instance e = ins.get(0);
            return e;
        }
        Instance e = new Instance();
        e.setIp( "localhost" );
        e.setPort( 80 );
        return e;
    }
}
