package com.offcn.common;

import com.offcn.pojo.TbSpecification;
import com.offcn.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

public class Specfication implements Serializable {

    private TbSpecification specification;
    private List<TbSpecificationOption> spectionOptionList;

    public TbSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(TbSpecification specification) {
        this.specification = specification;
    }

    public List<TbSpecificationOption> getSpectionOptionList() {
        return spectionOptionList;
    }

    public void setSpectionOptionList(List<TbSpecificationOption> spectionOptionList) {
        this.spectionOptionList = spectionOptionList;
    }
}
