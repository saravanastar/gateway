package com.jbhunt.fms.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class PreFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        // Add a custom header in the request
        ctx.addZuulRequestHeader("Authorization",
                request.getHeader("Authorization"));
        log.info(String.format("%s request to %s", request.getMethod(),
                request.getRequestURL().toString()));
        return null;
    }
}
