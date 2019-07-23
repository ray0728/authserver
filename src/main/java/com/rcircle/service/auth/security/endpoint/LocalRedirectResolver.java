package com.rcircle.service.auth.security.endpoint;

import com.rcircle.service.auth.redis.RedisStringUtils;
import com.rcircle.service.auth.service.GatewayService;
import com.rcircle.service.auth.utils.Toolkit;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.endpoint.RedirectResolver;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class LocalRedirectResolver implements RedirectResolver {
    private GatewayService gatewayService;
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
        if(!redisStringUtils.getValue(Toolkit.getIpAddr(request)).equals(token)){
            throw new RedirectMismatchException("Invalid trust token: " + token);
        }
        return requestedRedirect;
//        String gw = gatewayService.getGatewayIpAndPort();
//        if(requestedRedirect.substring(requestedRedirect.indexOf("//") + 2).equals(gw+"/rst/redirect")) {
//            return requestedRedirect;
//        }else{
//            throw new RedirectMismatchException("Invalid redirect: " + requestedRedirect + " does not match one of the registered values.[" + gw + "/rst/redirect]");
//        }
    }
}
