//错误提示
function showError(id, msg) {
    $("#" + id + "Ok").hide();
    $("#" + id + "Err").html("<i></i><p>" + msg + "</p>");
    $("#" + id + "Err").show();
    $("#" + id).addClass("input-red");
}

//错误隐藏
function hideError(id) {
    $("#" + id + "Err").hide();
    $("#" + id + "Err").html("");
    $("#" + id).removeClass("input-red");
}

//显示成功
function showSuccess(id) {
    $("#" + id + "Err").hide();
    $("#" + id + "Err").html("");
    $("#" + id + "Ok").show();
    $("#" + id).removeClass("input-red");
}


//打开注册协议弹层
function alertBox(maskid, bosid) {
    $("#" + maskid).show();
    $("#" + bosid).show();
}

//关闭注册协议弹层
function closeBox(maskid, bosid) {
    $("#" + maskid).hide();
    $("#" + bosid).hide();
}

//注册协议确认
$(function () {
    $("#agree").click(function () {
        var ischeck = document.getElementById("agree").checked;
        if (ischeck) {
            $("#btnRegist").attr("disabled", false);
            $("#btnRegist").removeClass("fail");
        } else {
            $("#btnRegist").attr("disabled", "disabled");
            $("#btnRegist").addClass("fail");
        }
    });


    var phone_tag = 0;
    var phone = null;
    $("#phone").blur(function () {
        phone_tag = 0;
        //获取输入的手机号的值，去掉空格
        phone = $.trim($("#phone").val());

        //1.手机号不能为空
        if (phone == null || phone == '') {
            showError("phone", "手机号不能为空");
            return;
        }

        //2.手机号长度合规
        if (phone.length != 11) {
            showError("phone", "手机号长度错误");
            return;
        }

        //3.手机号格式
        if (!/^1[1-9]\d{9}$/.test(phone)) {
            showError("phone", "手机号格式错误");
            return;
        }

        //4.手机号是否注册过
        $.get("/005-money-web/checkPhone", {phone: phone}, function (data) {
            if (data.code == 1) {
                showSuccess("phone");
                if (phone_tag == 1 && loginPassword_tag == 1 && messageCode_tag == 1) {
                    $.post("/005-money-web/addUser", {
                        phone: phone,
                        loginPassword: $.md5(loginPassword),
                        messageCode: messageCode
                    }, function (data) {
                        //返回成功跳转
                        if (data.code == 1) {
                            window.location.href = "/005-money-web/loan/page/realName";
                        }
                        if (data.code == 0) {
                            alert(data.message);
                        }


                    })
                }
            }
            if (data.code == 0) {
                showError("phone", "该手机号已被注册")
            }
        });

        showSuccess("phone");
        phone_tag = 1;
        return;
    });

    var loginPassword_tag = 0;
    var loginPassword = null;
    $("#loginPassword").blur(function () {

        loginPassword_tag = 0;
        loginPassword = $.trim($("#loginPassword").val());
        //1.密码不能为空
        if (loginPassword == null || loginPassword == '') {
            showError("loginPassword", "密码不能为空");
            return;
        }
        //2.密码长度
        if (loginPassword.length < 6 || loginPassword.length > 20) {
            showError("loginPassword", "密码长度错误");
            return;
        }
        //3.密码只能是数字和字母
        if (!/^[0-9a-zA-Z]+$/.test(loginPassword)) {
            showError("loginPassword", "密码只能是数字和字母");
            return;
        }
        //4.必须是数字和字母的组合
        if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)) {
            showError("loginPassword", "密码必须是数字和字母");
            return;
        }

        showSuccess("loginPassword");
        loginPassword_tag = 1;
        return true;
    });

    //发送验证码短信
    var code = 0;
    $("#messageCodeBtn").click(function () {
        //先判断电话和密码是否正确
        $("#phone").blur();
        $("#loginPassword").blur();
        if (phone_tag == 1 && loginPassword_tag == 1) {
            //获取电话号码传入后台
            var phone = $.trim($("#phone").val());
            var _this = $(this);
            if (!$(this).hasClass("on")) {
                $.get("/005-money-web/loan/page/messageCode", {phone: phone}, function (data) {
                    //后台发起请求，先返回一个随机验证码
                    alert(data.message);
                    code = data.message.length;
                    if (data.code == 1) {
                        $.leftTime(60, function (d) {
                            if (d.status) {
                                _this.addClass("on");
                                _this.html((d.s == "00" ? "60" : d.s) + "秒后重新获取");
                            } else {
                                _this.removeClass("on");
                                _this.html("获取验证码");
                            }
                        });
                    }
                });
            }
        }
    });

    //只是验证验证码格式是否正确
    var messageCode_tag = 0;
    var messageCode = null;
    $("#messageCode").blur(function () {
        messageCode_tag = 0;
        messageCode = $.trim($("#messageCode").val());
        // 不能为空
        if (messageCode == null || messageCode == '') {
            showError("messageCode", "验证码不能为空");
            return;
        }
        // 长度
        if (messageCode.length != code) {
            showError("messageCode", "长度错误");
            return;
        }
        // 数字
        if (!/^[0-9]*$/.test(messageCode)) {
            showError("messageCode", "只能为数字");
            return;
        }


        showSuccess("messageCode");
        messageCode_tag = 1;
    });

    //点击注册验证验证码格式和是否匹配
    $("#btnRegist").click(function () {
        $("#phone").blur();
        $("#loginPassword").blur();
        $("#messageCode").blur();
    });
});
