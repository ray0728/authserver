package com.rcircle.service.auth.controller;

import com.rcircle.service.auth.redis.RedisStringUtils;
import com.rcircle.service.auth.utils.Toolkit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/trust/")
public class TrustedController {
    @Value("${trust.config.id}")
    private String trustConfigId;

    @Value("${trust.config.secret}")
    private String trustConfigSecret;

    @Value("${trust.config.header}")
    private String trustConfigHeader;

    @Autowired
    private RedisStringUtils redisString;

    @GetMapping("token")
    public String getTrustToken(HttpServletRequest request, HttpServletResponse response, @RequestParam(name = "id") String id,
                                @RequestParam(name = "secret") String secret) {
        String token = null;
        if (trustConfigId.equals(id) && trustConfigSecret.equals(secret)) {
            String key = Toolkit.getIpAddr(request);
            token = redisString.getValue(key);
            if(token == null) {
                token = Toolkit.randomString(8);
                redisString.setKey(key, token);
            }
            return trustConfigHeader + ":" + token;
        }
        response.setStatus(401);
        return "";
    }

    @GetMapping("check")
    public String checkToken(HttpServletRequest request, HttpServletResponse response, @RequestParam(name = "token", required = false, defaultValue = "") String token) {
        if (token.equals("")) {
            token = request.getHeader(trustConfigHeader);
        }
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
        } else if (!redisString.getValue(Toolkit.getIpAddr(request)).equals(token)) {
            response.setStatus(402);
        }
        return "";
    }
}
