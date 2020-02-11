//服务层
app.service('userService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.register=function(entity){
		return $http.post('../user/register.do',entity);
	}

    this.getLoginName = function () {
        return $http.get("../login/getName.do");
    }


});