package com.offcn.service;

import com.offcn.common.PageResult;
import com.offcn.pojo.TbBrand;

import java.util.List;

public interface TbBrandService {

    /**
     * 查询所有的品牌
     */
    List<TbBrand> findAll();

    /**
     * 分页查询
     */
    PageResult findPage(int pageNum, int pageSize);

    void add(TbBrand tbBrand);

    TbBrand findOne(long id);

    void update(TbBrand tbBrand);

    /**
     * 批量删除
     */
    void delete(long [] ids);
}
