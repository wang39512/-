package com.offcn.service.impl;
import java.util.List;
import java.util.Map;

import com.offcn.common.Specfication;
import com.offcn.dao.TbSpecificationOptionMapper;
import com.offcn.pojo.TbSpecificationOption;
import com.offcn.pojo.TbSpecificationOptionExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.dao.TbSpecificationMapper;
import com.offcn.pojo.TbSpecification;
import com.offcn.pojo.TbSpecificationExample;
import com.offcn.pojo.TbSpecificationExample.Criteria;
import com.offcn.service.SpecificationService;

import com.offcn.common.PageResult;
import org.springframework.stereotype.Service;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specfication specification) {
		// 添加规格
		specificationMapper.insert(specification.getSpecification());
		// 添加规格选项
		for(TbSpecificationOption specificationOption : specification.getSpectionOptionList()){
			// 获取规格的id
			Long specficationId = specification.getSpecification().getId();
			specificationOption.setSpecId(specficationId);
			specificationOptionMapper.insert(specificationOption);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specfication specification){
		// 更新规格
		specificationMapper.updateByPrimaryKey(specification.getSpecification());
		// 更新规格选项（先把原来的规格删除，然后在重新添加规格选项）
		// 1、删除当前规格对应的规格选项
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(specification.getSpecification().getId());
		specificationOptionMapper.deleteByExample(example);
		// 2、重新添加规格
		// 添加规格选项
		for(TbSpecificationOption specificationOption : specification.getSpectionOptionList()){
			// 获取规格的id
			Long specficationId = specification.getSpecification().getId();
			specificationOption.setSpecId(specficationId);
			specificationOptionMapper.insert(specificationOption);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specfication findOne(Long id){
		// 根据规格id查询规格
		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
		// 根据规格id查询规格选项
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(id);
		List<TbSpecificationOption> specificationOptions = specificationOptionMapper.selectByExample(example);
		Specfication specfication = new Specfication();
		specfication.setSpecification(tbSpecification);
		specfication.setSpectionOptionList(specificationOptions);
		return specfication;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		// 删除规格的同时，要把当前规格对应的规格选项一起删除
		for(Long id:ids){
			// 删除规格
			specificationMapper.deleteByPrimaryKey(id);
			// 删除规格选项
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(id);
			specificationOptionMapper.deleteByExample(example);
		}		
	}


	/**
	 *下面这个分页查询只是多了一个条件参数
	 * TbSpecification specification，以此参数为条件，进行模糊查询
	 */

		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

			TbSpecificationExample example=new TbSpecificationExample();
			Criteria criteria = example.createCriteria();

			if(specification!=null){
				if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
					criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
				}
			}


			Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}
	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> selectSepcfications() {
		return specificationMapper.selectSpecfications();
	}

}
