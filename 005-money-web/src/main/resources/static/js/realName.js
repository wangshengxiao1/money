
//同意实名认证协议
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});

	//电话号码格式判定
	var phone_tag = 0;
	var phone = null;
	$("#phone").blur(function () {
		phone_tag = 0;
		//获取输入的手机号的值，去掉空格
		phone = $.trim($("#phone").val());

		//1.手机号不能为空
		if (phone == null || phone == ''){
			showError("phone","手机号不能为空");
			return;
		}

		//2.手机号长度合规
		if (phone.length != 11) {
			showError("phone","手机号长度错误");
			return;
		}

		//3.手机号格式
		if (!/^1[1-9]\d{9}$/.test(phone)){
			showError("phone","手机号格式错误");
			return;
		}

		//手机号是否注册过无所谓，可以重复
		showSuccess("phone");
		phone_tag = 1;
	});

	//真实姓名判定
	var realName_tag = 0;
	var realName = null;
	$("#realName").blur(function () {
		realName_tag = 0;
		if (phone_tag==1){
			realName = $.trim($("#realName").val());
			//1.姓名不能为空
			if (realName == '' || realName == null) {
				showError("realName","姓名不能为空");
				return;
			}
			//2.姓名必须为汉字
			if (!/^[\u4e00-\u9fa5]{0,}$/.test(realName)){
				showError("realName","姓名必须为汉字");
				return;
			}

			//3.姓名长度在2到6之间
			if (realName.length<2||realName.length>6){
				showError("realName","姓名长度在2-6之间");
				return;
			}

			showSuccess("realName");
			realName_tag=1;
		} else {
			showError("realName","请输入手机号码");
		}

	});

	//身份证号判定
	var idCard_tag = 0;
	var idCard = null;
	$("#idCard").blur(function () {
		idCard_tag = 0;
		if (realName_tag==1){
			var idCard = $.trim($("#idCard").val());
			//1.身份证号不能为空
			if (idCard==null||idCard==''){
				showError("idCard","身份证号不能为空");
				return;
			}
			//2.身份证号格式
			if (!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)){
				showError("idCard","身份证号格式错误");
				return;
			}
			//3.身份证号和姓名匹配
			$.get("/005-money-web/loan/page/checkIdCard",{realName:realName,idCard:idCard},function (data) {
				phone = $.trim($("#phone").val());
				if (data.code==0){
					alert(data.message);
				}
				if (data.code==1){
					alert(data.message);
					//判断成功往后台传值
					$.post("/005-money-web/addRealName",{phone:phone,realName:realName,idCard:idCard},function (data) {
						alert(data)
					})
					showSuccess("idCard");

				}
			});

			showSuccess("idCard");
			idCard_tag=1;
		} else {
			showError("idCard","请输入正确的姓名");
		}

	});


	//只是验证验证码格式是否正确
	var messageCode_tag=0;
	var messageCode = null;
	$("#messageCode").blur(function () {
		messageCode_tag=0;
		messageCode = $.trim($("#messageCode").val());
		// 不能为空
		if (messageCode==null||messageCode==''){
			showError("messageCode","验证码不能为空");
			return;
		}
		// 长度
		if (messageCode.length!=code){
			showError("messageCode","长度错误");
			return;
		}
		// 数字
		if (!/^[0-9]*$/.test(messageCode)){
			showError("messageCode","只能为数字");
			return;
		}


		showSuccess("messageCode");
		messageCode_tag = 1;
	});

	//发送验证码短信
	//code为验证码长度
	var code = 0;
	$("#messageCodeBtn").click(function () {
		//只看身份证是否通过，只要身份证通过其他都通过
		//$("#idCard").blur();

		if (phone_tag==1 && idCard_tag==1 && realName_tag==1){
			//获取电话号码传入后台
			var phone = $.trim($("#phone").val());
			var _this = $(this);
			if (!$(this).hasClass("on")) {
				$.get("/005-money-web/loan/page/messageCode",{phone:phone},function (data) {
					//后台发起请求，先返回一个随机验证码
					alert(data.message);
					code = data.message.length
					alert(data.code + "ceshi");
					if(data.code==1){
						$.leftTime(60,function(d){
							if(d.status){
								_this.addClass("on");
								_this.html((d.s=="00"?"60":d.s)+"秒后重新获取");
							}else{
								_this.removeClass("on");
								_this.html("获取验证码");
							}
						});
					}
				});
			}
		}
	});



	//点击认证
	$("#btnRegist").click(function () {
		$("#idCard").blur();
	});
});
//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}

//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}

