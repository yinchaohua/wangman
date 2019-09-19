package com.xjwx.fognode;

import com.xjwx.fognode.config.DatasourceConfig;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RefreshScope
@RestController
@RequestMapping("/configController")
public class EurekaProviderController {

    @Resource
    private DatasourceConfig datasourceConfig;


    @GetMapping(value = "content" ,produces = "application/json;charset=UTF-8")
    public String home() {
        return "Hello world ,port:" + datasourceConfig.getConnectionProperties();
    }
}
