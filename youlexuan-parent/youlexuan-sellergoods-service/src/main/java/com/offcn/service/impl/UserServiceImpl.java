package com.offcn.service.impl;

import com.offcn.common.Md5Utils;
import com.offcn.dao.TbSellerMapper;
import com.offcn.pojo.TbSeller;
import com.offcn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TbSellerMapper sellerMapper;

    @Override
    public void registerUser(TbSeller seller) {
        // 对密码进行加密
        String pwd = Md5Utils.getPwd(seller.getPassword());
        seller.setPassword(pwd);
        // 注册完成以后，默认的状态是0 如果审批通过以后状态是1
        seller.setStatus("0");
        sellerMapper.insert(seller);
    }
}
