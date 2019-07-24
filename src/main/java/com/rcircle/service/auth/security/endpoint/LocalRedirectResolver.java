package com.rcircle.service.auth.security.endpoint;

import com.rcircle.service.auth.redis.RedisStringUtils;
import com.rcircle.service.auth.utils.Toolkit;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.endpoint.RedirectResolver;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class LocalRedirectResolver implements RedirectResolver {
    private String trustConfigHeader;
    private RedisStringUtils redisStringUtils;
    public LocalRedirectResolver setGatewayService(RedisStringUtils rsu, String headerstr){
        trustConfigHeader = headerstr;
        redisStringUtils = rsu;
        return this;
    }
    public String resolveRedirect(String requestedRedirect, ClientDetails client) throws OAuth2Exception {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String token = request.getHeader(trustConfigHeader);
        String cache_token = redisStringUtils.getValue(Toolkit.getIpAddr(request));
        if(cache_token == null || !cache_token.equals(token)){
            throw new RedirectMismatchException("Invalid trust token: " + token);
        }
        return requestedRedirect;
    }
}
