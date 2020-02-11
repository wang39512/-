//服务层
app.service('loginService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.getLoginName=function(){
		return $http.get('../login/getName.do');
	}

});