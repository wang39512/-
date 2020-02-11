package com.offcn.authentication;

import com.offcn.pojo.TbSeller;
import com.offcn.service.SellerService;
import com.offcn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 构建角色
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        // 获取当前商家的密码
        TbSeller seller = sellerService.findOne(username);
        if(seller != null){
            // 在登录的时候要保证，商家时审核通过的
            if(seller.getStatus().equals("1")){
                return new User(username,seller.getPassword(),grantedAuthorities);
            }
        }else{
            return  null;
        }
        return null;
    }
}
