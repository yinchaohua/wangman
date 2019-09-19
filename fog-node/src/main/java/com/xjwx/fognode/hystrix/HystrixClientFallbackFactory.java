package com.xjwx.fognode.hystrix;

import com.xjwx.fognode.FeignClient.ConfigClient;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class HystrixClientFallbackFactory implements FallbackFactory<ConfigClient> {


    @Override
    public ConfigClient create(Throwable throwable) {
        return new ConfigClient() {
            @Override
            public String consumer() {
                return "feign + hystrix ,提供者服务挂了";
            }
        };
    }
}
