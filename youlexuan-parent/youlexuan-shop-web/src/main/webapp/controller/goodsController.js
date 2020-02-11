 //控制层 
app.controller('goodsController' ,function($scope,$controller ,$location,itemCatService,typeTemplateService ,goodsService,fileUpload){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
		var id = $location.search()['id']
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				// 获取富文本编辑器的结果
                editor.html($scope.entity.tbGoodsDesc.introduction);
				// 显示图片
				$scope.entity.tbGoodsDesc.itemImages = JSON.parse($scope.entity.tbGoodsDesc.itemImages);
				// 扩展属性(注意:在模板添加的地方以已经对customAttributeItems进行了转json对象的操作，所有在这里在转对象会冲突，在模板添加的地方，对当前的id进行判断)
                $scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse($scope.entity.tbGoodsDesc.customAttributeItems);
                // 显示规格选项
				$scope.entity.tbGoodsDesc.specificationItems = JSON.parse($scope.entity.tbGoodsDesc.specificationItems);
				// 显示规格选中后的列表
				for(var i = 0; i < $scope.entity.tbItemList.length; i++){
						$scope.entity.tbItemList[i].spec = JSON.parse($scope.entity.tbItemList[i].spec);
				}
			}
		);
	}

	// 判断规格是否勾选
	$scope.checkSpecIsChecked = function(specName,optionName){
		var items = $scope.entity.tbGoodsDesc.specificationItems;
        var obj = $scope.findValueByKey(items,'attributeName',specName);
        if(obj == null){
        	return false;
		}else{
        	if(obj.attributeValue.indexOf(optionName) >= 0){
        		return true;
			}else{
        		return false;
			}
		}
	}
	
	//保存 
	$scope.save=function(){
		// 获取富文本编辑器的内容（前提是要对富文本编辑器初始化）
        $scope.entity.tbGoodsDesc.introduction=editor.html();
		// 应为在controller.js中把数据库获取的json字符串转成了对象，当需要添加数据到数据库
		//  时，同时需要再把转过的对象在还原为json字符串
		$scope.entity.tbGoodsDesc.customAttributeItems = JSON.stringify($scope.entity.tbGoodsDesc.customAttributeItems);
        $scope.entity.tbGoodsDesc.itemImages = JSON.stringify($scope.entity.tbGoodsDesc.itemImages);
        $scope.entity.tbGoodsDesc.specificationItems = JSON.stringify($scope.entity.tbGoodsDesc.specificationItems);
        // 转化spec
		if($scope.entity.tbGoods.isEnableSpec == 1){
            for(var i = 0; i < $scope.entity.tbItemList.length;i++){
                $scope.entity.tbItemList[i].spec  = JSON.stringify($scope.entity.tbItemList[i].spec);
            }
		}

		if($scope.entity.tbGoods.id != null){
			goodsService.update($scope.entity).success(
				function (response) {
                    if(response.success){
                        // 刷新页面
                        window.location.reload();
                    }else{
                        alert(response.msg);
                    }
                }
			)
		}else{
            goodsService.add($scope.entity).success(
                function (response) {
                    if(response.success){
                        // 刷新页面
                        window.location.reload();
                    }else{
                        alert(response.msg);
                    }
                }
            );
		}
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	// 查询分类
	// 获取一级分类
	$scope.selectItemCatOneList = function () {
			itemCatService.findItemCatByPid(0).success(
				function (response) {
					$scope.itemCatOneList = response;
                }
			)
    }
    // 获取二级分类
	$scope.$watch('entity.tbGoods.category1Id',function (oldValue,newValue) {
		if(oldValue){
			// 如果一级分类选中了结果，开始查询二级分类
            itemCatService.findItemCatByPid(oldValue).success(
            	function (response) {
                    $scope.itemCatTwoList = response;
                }
			)
		}
    },true);
    // 获取三级分类
	$scope.$watch('entity.tbGoods.category2Id',function (oldValue,newValue) {

		if(oldValue){
			// 如果一级分类选中了结果，开始查询二级分类
            itemCatService.findItemCatByPid(oldValue).success(
            	function (response) {
                    $scope.itemCatThreeList = response;
                }
			)
		}
    })
    // 获取模板的id
	$scope.$watch('entity.tbGoods.category3Id',function (oldValue,newValue) {
		if(oldValue){
			// 在查询模板的时候已经是最后一级，没有父id了，直接通过id查询结果
            itemCatService.findOne(oldValue).success(
            	function (response) {
                    $scope.entity.tbGoods.typeTemplateId = response.typeId;
                }
			)
		}
    });
	// 查询品牌
    $scope.$watch('entity.tbGoods.typeTemplateId',function (oldValue,newValue) {
        if(oldValue){
            // 在查询模板的时候已经是最后一级，没有父id了，直接通过id查询结果
            typeTemplateService.findOne(oldValue).success(
                function (response) {
                	// 模板id查询品牌
                   $scope.brandList = JSON.parse(response.brandIds)
					// 根据模板id查询扩展属性
					if($location.search()['id'] == null){
                        $scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
                    }
                }
            );
            // 查询规格列表
			typeTemplateService.selectSpecList(oldValue).success(
					function (response) {
						$scope.specList = response;
                    }
				)
       		 }
    });

    $scope.entity = {tbGoods:{},tbGoodsDesc:{itemImages:[],specificationItems:[]}}
    // 更新选中结果的集合
	$scope.updateSelctValue = function($event,name,value){
		var object = $scope.findValueByKey($scope.entity.tbGoodsDesc.specificationItems,'attributeName',name);
		if(object != null){
			// 勾选
			if($event.target.checked){
				object.attributeValue.push(value);
			}else{// 取消勾选
				object.attributeValue.splice(object.attributeValue.indexOf(value),1);
				// 取消勾选，如果全部取消了，应在在大的集合中移除小的集合
				if(object.attributeValue.length ==0){
				   $scope.entity.tbGoodsDesc.specificationItems.splice($scope.entity.tbGoodsDesc.specificationItems.indexOf(object,1));
				}
            }
		}else{
			// 没有数据，初始化
			$scope.entity.tbGoodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
		}
	}

	// 把选中的规格结果存放到新的集合
	 $scope.createSpecList = function(){
    	// 初始化新的集合
		 $scope.entity.tbItemList = [{spec:{},price:0,num:0,isDefault:0}];
             // var items = JSON.parse($scope.entity.tbGoodsDesc.specificationItems);
		     //  因为在添加商品的时候会调用findone(),会执行一次JSON.parse($scope.entity.tbGoodsDesc.specificationItems);，所以当实现商品修改以后，这里就不需要再次转型了
			 var items = $scope.entity.tbGoodsDesc.specificationItems;
             for(var i = 0; i <items.length; i++){
                 $scope.entity.tbItemList = $scope.addColumn($scope.entity.tbItemList,items[i].attributeName,items[i].attributeValue);
         }
	 }

	 $scope.addColumn = function(list,name,value){
    	var newList  =[];
    	for(var i = 0; i < list.length; i++){
    		var row = list[i];
    		for(var j = 0; j < value.length; j++ ){
    			// 下载的row是一个json字符串，需要把它转成对象
				var newRow = JSON.parse(JSON.stringify(row));
				newRow.spec[name] = value[j];
				// 第一次遍历
				//  i = 0   row   pingmuchicun 4.5寸  5寸
				//  j = 0   4.5寸
                //  j = 1   5寸
				// 第二次遍历
				//  i = 1   row   jishenneicun 16G  32G   64G
				//  j = 0   16G
				//  j = 1    32G
				//  j = 2    64G
				newList.push(newRow);
			}
		}
		return newList;
	 }





    // 图片上传
    $scope.fileUpload = function () {
        fileUpload.fileUpload().success(
            function (response) {
                $scope.entity_image.url = response;
                console.log($scope.entity_image.url);
            }
        )
    }

	// 保存图片到集合
	$scope.addImage = function () {
		$scope.entity.tbGoodsDesc.itemImages.push($scope.entity_image);
    }
    // 从集合中移除图片
    $scope.deleteImage = function (index) {
        $scope.entity.tbGoodsDesc.itemImages.splice(index,1);
    }


    // 处理分类显示问题
	$scope.itemCatList = [];
    $scope.findItemCatList = function () {
		itemCatService.findAll().success(
			function (response) {
				for(var i = 0; i < response.length; i++){
                    $scope.itemCatList[response[i].id] =  response[i].name;
				}
            }
		)
    }
	// 处理状态显示问题
	$scope.statusList = ['未申请',"申请中","审核通过","审核驳回"];


    // 商品的审核
    $scope.updateStatus = function (status) {
        goodsService.updateStatus($scope.selectIds,status).success(
            function (response) {
                if(response.success){
                    $scope.reloadList();
                    $scope.selectIds=[];
                }else{
                    alert(response.msg);
                }
            }
        )
    }
});	
























