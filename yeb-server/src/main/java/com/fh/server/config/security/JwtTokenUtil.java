package com.fh.server.config.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtToken工具类
 *
 * @author fanghao
 * @since 2022/1/12
 */
@Component
public class JwtTokenUtil {
    //用户名和过期时间
    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_CREATED = "created";
    @Value("${jwt.secret}")
    private String secret; // JWT 加解密使用的密钥
    @Value("${jwt.expiration}")
    private Long expiration; // JWT 的超期限时间 （ 60*60*24）24小时失效



    // 可供外界调用的方法：
    // 1. 根据用户信息生成 token
    // 2. 从 token 中获取登录 用户名
    // 3. 验证 token 是否有效
    // 4. 判断 token 是否可以被刷新
    // 5. 刷新 token

    /**
     * 1.  根据用户信息生成 token
     * 用户信息从 security 框架 UserDetails 中获取
     *
     * @param userDetails
     * @return
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(); // 准备存放 token 的容器（荷载）
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername()); // 从 security 框架 UserDetails 中获取用户名
        claims.put(CLAIM_KEY_CREATED, new Date()); // 创建时间为当前时间
        return generateToken(claims); // 增加其它信息（ 本类内新建方法）
    }

    /**
     * 从token获取登录用户名
     * @return
     */
    public String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);  // 根据 token 获取荷载（ 本类内新建方法 ）
            username = claims.getSubject();             // 通过荷载调用 getSubject 方法，获取用户名
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 判断token是否有效 1.判断token是否过期  2. token荷载用户名和userdetails用户名是否一直
     * @param token
     * @param userDetails
     * @return
     */
    public boolean validateToken(String token, UserDetails userDetails){
        String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * 判断token是否失效
     * @param token
     * @return
     */
    private boolean isTokenExpired(String token) {
       Date expireDate =  getExpiredDateFromToken(token);
       return expireDate.before(new Date());             //判断token过期时间是否在当前时间之前 如果是 则token失效
    }

    /**
     * 从 token 获取时间
     * @param token
     * @return
     */
    private Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);          //根据token获取荷载
        return claims.getExpiration();                //根据荷载获取过期时间
    }

    /**
     * 判断 token 是否可以被刷新
     * 过期了，可以刷新。获取有效时间方法取反为过期
     * @param token
     * @return
     */
    public boolean canRefresh(String token){
        return !isTokenExpired(token);              //token过期了 可以刷新 没失效的token不能刷新
    }

    /**
     * 刷新token
     * @param token
     * @return
     */
    public String refreshToken(String token){
        Claims claims = getClaimsFromToken(token);  //根据token获取荷载
        claims.put(CLAIM_KEY_CREATED, new Date());  //通过荷载把创建时间设置为 当前时间 = 刷新 token 过期时间
        return generateToken(claims);               //重新生成token
    }

    /**
     * 从token获取荷载
     * @param token
     * @return
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)          //密钥
                    .parseClaimsJws(token)          //签名
                    .getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return claims;
    }

    /**
     * 根据容器(荷载)生成 JWT TOKEN
     * @param claims
     * @return
     */
    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)                              //荷载
                .setExpiration(generateExpiration())            //过期时间，需要类型转换(本类新建方法)
                .signWith(SignatureAlgorithm.ES256, secret)     //设置签名 指定算法
                .compact();
    }

    /**
     * 生成token失效时间
     * @return
     */
    private Date generateExpiration() {
        // 失效时间为：当前时间 + 配置的过期时间
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

}