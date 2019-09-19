package com.xjwx.fognode.FeignClient;

import com.xjwx.fognode.hystrix.HystrixClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "eureka-provider", fallbackFactory = HystrixClientFallbackFactory.class)
public interface ConfigClient {

    @GetMapping("/configController/content")
    String consumer();

}
