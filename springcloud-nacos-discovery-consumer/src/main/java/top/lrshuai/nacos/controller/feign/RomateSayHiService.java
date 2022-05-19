package top.lrshuai.nacos.controller.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**此处需要是注册进nacos的服务名称(ServiceId)，如果不一致会报错*/
//@FeignClient(value = "nacos-provider")
public interface RomateSayHiService {

    /**方法签名要和注册服务中的一致*/
    @GetMapping(value = "/payment/get/{id}")
    CommonResult<String> getPaymentById(@PathVariable("name") String name);
}
