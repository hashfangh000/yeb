package com.fh.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.fh.server.config.security.JwtTokenUtil;
import com.fh.server.mapper.AdminRoleMapper;
import com.fh.server.pojo.Admin;
import com.fh.server.mapper.AdminMapper;
import com.fh.server.pojo.RespBean;
import com.fh.server.service.IAdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Jiayu.Yang
 * @since 2021-11-16
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Value("${jwt.tokenHead}")
    private String tokenHead;


    /**
     * 功能: 登录之后返回token
     * * @param username
     *
     * @param password
     * @param request
     * @return {@link RespBean}
     * @Date 2021/11/16
     * @author jiayu.Yang
     */
    @Override
    public RespBean login(String username, String password, HttpServletRequest request) {
        //校验验证码是否有效
//        String captcha = (String) request.getSession().getAttribute("captcha");
//        if (!captcha.equalsIgnoreCase(code)) {
//            return RespBean.error("验证码错误");
//        }
        //根据前端传回的username加载UserDetails
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails == null || !passwordEncoder.matches(password, userDetails.getPassword())) {
            return RespBean.error("用户名或密码不正确");
        }
        if (!userDetails.isEnabled()) {
            return RespBean.error("账号被禁用，请联系管理员");
        }

        //（更新security对象）
        UsernamePasswordAuthenticationToken authenticationToken = new
                UsernamePasswordAuthenticationToken(userDetails
                , null, userDetails.getAuthorities());
        //将当前对象放在SpringSecurity全局配置中
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        //根据UserDetails生成token
        String token = jwtTokenUtil.generateToken(userDetails);
        HashMap<String, String> tokenMap = new HashMap<>();
        tokenMap.put("tokenHead", tokenHead);
        tokenMap.put("token", token);
        return RespBean.success("登录成功", tokenMap);
    }

    /**
     * 功能: 根据用户名获取用户详情
     */
    @Override
    public Admin getAdminByUserName(String username) {
        return adminMapper.selectOne(new QueryWrapper<Admin>().eq("username", username).eq("enabled", true));
    }
}