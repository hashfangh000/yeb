package com.fh.server.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Jwt登录授权过滤器
 *
 * @author fanghao on 2022/1/13
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;         //jwt 存储的请求头 ( key )
    @Value("${jwt.tokenHead}")
    private String tokenHead;           //jwt 负载中拿到的 开头部分 ( value )
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(tokenHeader);         //根据 key 获取 value (要验证的头)
        //存在token
        //如果拿到 value 并且是根据 Bearer 开头的
        if (null != authHeader && authHeader.startsWith(tokenHeader)){
            //截取字符串
            String authToken = authHeader.substring(tokenHeader.length());
            String username = jwtTokenUtil.getUsernameFromToken(authToken);
            //token存在但用户名未登录
            if (username != null && null == SecurityContextHolder.getContext().getAuthentication()){
                //登录  通过username 拿到 UserDetails
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                //判断token是否有效，重新设置用户对象
                if (jwtTokenUtil.validateToken(authToken, userDetails)){
                    //参数 ：  用户对象 密码 角色
                    UsernamePasswordAuthenticationToken authenticationToken = new
                            UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}