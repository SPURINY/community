$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");
	//获取标题和内容
	var title=$("#recipient-name").val();/*id选择器*/
	var content=$("#message-text").val();
	//发送异步请求
	$.post(
		CONTEXT_PATH+"/discuss/add",
		{"title":title,"content":content},
		function(data){
			data= $.parseJSON(data);//字符串转对象
			//获取提示框对应的元素，并设置内容
			$("#hintBody").text(data.msg);
			//显示提示框，两秒后提示框自动消失
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//如果发布成功，刷新页面(reload)
				if(data.code==0){
					window.location.reload();
				}
			}, 2000);

		}
	);

}