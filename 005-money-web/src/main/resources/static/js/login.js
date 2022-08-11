
var referrer = "";//登录后返回页面
referrer = document.referrer;
if (!referrer) {
	try {
		if (window.opener) {                
			// IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性              
			referrer = window.opener.location.href;
		}  
	} catch (e) {
	}
}

//按键盘Enter键即可登录
$(document).keyup(function(event){
	if(event.keyCode == 13){
		login();
	}
});


function Login() {
	var phone = $.trim($("#phone").val());
	var loginPassword = $.trim($("#loginPassword").val());
	if (phone==null||phone==''){
		$("#phone").focus();
		return;
	}
	if (loginPassword==null||loginPassword==''){
		$("#loginPassword").focus();
		return;
	}

	//密码需要加密传输
	$.post("/005-money-web/loan/page/loginSubmit",{phone:phone,loginPassword:$.md5(loginPassword)},function (data) {
		if (data.code==1){
			window.location.href=$("#ReturnUrl").val();
		}
		if (data.code==0){
			alert(data.message);
		}
	});

}