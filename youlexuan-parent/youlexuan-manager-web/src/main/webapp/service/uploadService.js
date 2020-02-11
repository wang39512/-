//服务层
app.service('uploadService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.uploadd=function(file){
		return $http.post('../up/uploadd.do',file);
	}
});