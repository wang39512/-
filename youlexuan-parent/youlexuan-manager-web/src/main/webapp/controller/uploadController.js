 //控制层 
app.controller('uploadController' ,function($scope,$controller,uploadService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.uploadd=function(){
        console.log("aaaaaa");
        uploadService.uploadd(file).success(

            function(response){
                $scope.imgUrl=response;
            }
        );
	}

    $scope.demo=function(){
        console.log("aaaaaa");
    }
});	