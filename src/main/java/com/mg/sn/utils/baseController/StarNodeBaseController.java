package com.mg.sn.utils.baseController;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mg.sn.utils.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller工具类
 */
public class StarNodeBaseController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private RedisUtil redisUtil;

    public StarNodeBaseController () {}

    /**
     * 获取用户id
     * @return
     */
    public String getUserId () {
        String userId = "";
        String token = request.getHeader("token");
        if (redisUtil.hasKey(token)) {
            Object tokenObject = redisUtil.get(token);
            if (tokenObject instanceof Map) {
                HashMap<String, Object> tokenMap = (HashMap<String, Object>) tokenObject;
                userId = tokenMap.get("userId").toString();
            }
        }
        return StringUtils.isEmpty(userId) ? "1" : userId;
    }
}
