package com.offcn.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.offcn.common.Good;
import com.offcn.dao.TbBrandMapper;
import com.offcn.dao.TbGoodsDescMapper;
import com.offcn.dao.TbItemMapper;
import com.offcn.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.dao.TbGoodsMapper;
import com.offcn.pojo.TbGoodsExample.Criteria;
import com.offcn.service.GoodsService;

import com.offcn.common.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Good goods) {
		// 添加数据到tbGoods
		// 指定商品是属于哪个商家（这个在controller中实现，因为在controller中方便获取当前登录的商家名）
		// 设置添加商品的状态audit_status    0 刚添加的商品  1 提交审核  审核通过 2   审核驳回 3
		// 默认刚添加的商品是没有上架的  is_marketable    0没有上架    1 上架
		// 默认添加商品的删除状态是0 （没有删除）
		TbGoods goods1 = goods.getTbGoods();
		goods1.setIsDelete("0");
		goods1.setIsMarketable("0");
		goods1.setAuditStatus("0");
		goodsMapper.insert(goods1);
		//  添加数据到tbGoodsDesc
		TbGoodsDesc goodsDesc = goods.getTbGoodsDesc();
		// 给goodDesc设置good_id（前提条件是在goodsMapper中获取添加完成tbGoods的id）
		goodsDesc.setGoodsId(goods.getTbGoods().getId());
		goodsDescMapper.insert(goodsDesc);
		// 判断是否启用规格，如果启用规格就需要添加规格选中的数据，否则直接添加一个item
		if(goods.getTbGoods().getIsEnableSpec().equals("1")){
			//  添加数据到tbItems
			List<TbItem> tbItemList = goods.getTbItemList();
			for(TbItem tbItem : tbItemList){
				// 设置item的title（这个title是拼接来的  商品的名称+规格）
				String title = goods.getTbGoods().getGoodsName();
				Map<String,Object> specMap = JSON.parseObject(tbItem.getSpec());
				// {"pingmuchicun":"5.5寸","jishenneicun":"64G"}  要遍历取出5.5寸和64G
				for(String key : specMap.keySet()){
					title += specMap.get(key);
				}
				// 设置title
				tbItem.setTitle(title);
				// 设置图片（默认取第一张图片）
				List<Map> mapList = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(),Map.class);
				// [{"color":"黑色","url":"http://10.10.66.155/group1/M00/00/00/CgpCm11sfYeAIq7dAACjgX1Jdm4344.jpg","$$hashKey":"005"}]
				if(mapList.size() > 0){
					tbItem.setImage((String)mapList.get(0).get("url"));
				}
				// 设置categoryId（指向的就是三级的id）
				tbItem.setCategoryid(goods.getTbGoods().getCategory3Id());
				tbItem.setCategory(goods.getTbGoods().getGoodsName());
				// 设置商品的状态（要和tbGoods中的状态一致）
				tbItem.setCreateTime(new Date());
				tbItem.setUpdateTime(new Date());
				tbItem.setGoodsId(goods.getTbGoods().getId());
				tbItem.setSellerId(goods.getTbGoods().getSellerId());
				// 设置brand
				String name = brandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId()).getName();
				tbItem.setBrand(name);
				// 保存tbItem
				itemMapper.insert(tbItem);
			}
		}else{
			System.out.println("不启用规格的添加");
			// 没有启用规格
			TbItem item = new TbItem();
			// 如果不启用规格需要自己设置status和isdefault的值（因为现在是new的对象，在原来的entity中没法后去这两个结果值）
			item.setIsDefault("0");
			item.setStatus("0");
			// 设置brand
			String name = brandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId()).getName();
			item.setBrand(name);
			item.setSellerId(goods.getTbGoods().getSellerId());
			item.setGoodsId(goods.getTbGoods().getId());
			item.setUpdateTime(new Date());
			item.setCreateTime(new Date());
			item.setCategory(goods.getTbGoods().getGoodsName());
			item.setCategoryid(goods.getTbGoods().getCategory3Id());
			List<Map> mapList = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(),Map.class);
			// [{"color":"黑色","url":"http://10.10.66.155/group1/M00/00/00/CgpCm11sfYeAIq7dAACjgX1Jdm4344.jpg","$$hashKey":"005"}]
			if(mapList.size() > 0){
				item.setImage((String)mapList.get(0).get("url"));
			}
			item.setTitle(goods.getTbGoods().getGoodsName());
			item.setPrice(goods.getTbGoods().getPrice());
			item.setNum(9999);
			item.setSpec("{}");
			itemMapper.insert(item);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Good goods){
		// 修改tbGoods
		// 每次修改要把商品的状态置为0
		goods.getTbGoods().setAuditStatus("0");
		goods.getTbGoods().setIsMarketable("0");
		goodsMapper.updateByPrimaryKey(goods.getTbGoods());
		// 修改tbGoodsDesc
		goodsDescMapper.updateByPrimaryKey(goods.getTbGoodsDesc());

		if(goods.getTbGoods().getIsEnableSpec().equals("1")){
			// 修改tbItems
			// 先删除原有的sku列表，再重新添加
			TbItemExample example = new TbItemExample();
			TbItemExample.Criteria criteria = example.createCriteria();
			criteria.andGoodsIdEqualTo(goods.getTbGoods().getId());
			itemMapper.deleteByExample(example);
			// 重新添加sku
			// 判断是否启用规格，如果启用规格就需要添加规格选中的数据，否则直接添加一个item

			//  添加数据到tbItems
			List<TbItem> tbItemList = goods.getTbItemList();
			for(TbItem tbItem : tbItemList){
				// 设置item的title（这个title是拼接来的  商品的名称+规格）
				String title = goods.getTbGoods().getGoodsName();
				Map<String,Object> specMap = JSON.parseObject(tbItem.getSpec());
				// {"pingmuchicun":"5.5寸","jishenneicun":"64G"}  要遍历取出5.5寸和64G
				for(String key : specMap.keySet()){
					title += specMap.get(key);
				}
				// 设置title
				tbItem.setTitle(title);
				// 设置图片（默认取第一张图片）
				List<Map> mapList = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(),Map.class);
				// [{"color":"黑色","url":"http://10.10.66.155/group1/M00/00/00/CgpCm11sfYeAIq7dAACjgX1Jdm4344.jpg","$$hashKey":"005"}]
				if(mapList.size() > 0){
					tbItem.setImage((String)mapList.get(0).get("url"));
				}
				// 设置categoryId（指向的就是三级的id）
				tbItem.setCategoryid(goods.getTbGoods().getCategory3Id());
				tbItem.setCategory(goods.getTbGoods().getGoodsName());
				// 设置商品的状态（要和tbGoods中的状态一致）
				tbItem.setCreateTime(new Date());
				tbItem.setUpdateTime(new Date());
				tbItem.setGoodsId(goods.getTbGoods().getId());
				tbItem.setSellerId(goods.getTbGoods().getSellerId());
				// 设置brand
				String name = brandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId()).getName();
				tbItem.setBrand(name);
				// 保存tbItem
				itemMapper.insert(tbItem);
			}
		}else{
			System.out.println("不启用规格的添加");
			TbItemExample example = new TbItemExample();
			TbItemExample.Criteria criteria = example.createCriteria();
			criteria.andGoodsIdEqualTo(goods.getTbGoods().getId());
			List<TbItem> tbItems = itemMapper.selectByExample(example);
			TbItem item = tbItems.get(0);
			// 如果不启用规格需要自己设置status和isdefault的值（因为现在是new的对象，在原来的entity中没法后去这两个结果值）
			item.setIsDefault("0");
			item.setStatus("0");
			// 设置brand
			String name = brandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId()).getName();
			item.setBrand(name);
			item.setSellerId(goods.getTbGoods().getSellerId());
			item.setGoodsId(goods.getTbGoods().getId());
			item.setUpdateTime(new Date());
			item.setCreateTime(new Date());
			item.setCategory(goods.getTbGoods().getGoodsName());
			item.setCategoryid(goods.getTbGoods().getCategory3Id());
			List<Map> mapList = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(),Map.class);
			// [{"color":"黑色","url":"http://10.10.66.155/group1/M00/00/00/CgpCm11sfYeAIq7dAACjgX1Jdm4344.jpg","$$hashKey":"005"}]
			if(mapList.size() > 0){
				item.setImage((String)mapList.get(0).get("url"));
			}
			item.setTitle(goods.getTbGoods().getGoodsName());
			item.setPrice(goods.getTbGoods().getPrice());
			item.setNum(9999);
			item.setSpec("{}");
			itemMapper.updateByPrimaryKey(item);
		}

	}
	
	/**
	 * 根据ID获取实体
	 * @param id  goods的id
	 * @return
	 */
	@Override
	public Good findOne(Long id){
		// 查询tbGoods表
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		// 查询goodsDesc
		TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> tbItems = itemMapper.selectByExample(example);
		Good good = new Good();
		good.setTbGoods(tbGoods);
		good.setTbGoodsDesc(goodsDesc);
		good.setTbItemList(tbItems);
		return good;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			goodsMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateStatus(long[] ids, String status) {
		for(long id : ids){
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(tbGoods);
			// 修改和goods对应的item的商品的状态
			TbItemExample example = new TbItemExample();
			TbItemExample.Criteria criteria = example.createCriteria();
			criteria.andGoodsIdEqualTo(id);
			List<TbItem> tbItems = itemMapper.selectByExample(example);
			for(TbItem item : tbItems){
				item.setStatus(status);
				itemMapper.updateByPrimaryKey(item);
			}
		}
	}

}
