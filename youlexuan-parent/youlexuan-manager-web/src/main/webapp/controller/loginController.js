 //控制层 
app.controller('loginController' ,function($scope,$controller ,loginService){
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.getLoginName=function(){
		loginService.getLoginName().success(
			function(response){
				$scope.loginName = response.name;
			}			
		);
	}    
	

    
});	