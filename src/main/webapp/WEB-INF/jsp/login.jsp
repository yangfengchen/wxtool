<%@ page contentType="text/html;charset=UTF-8" language="java"  %>
<html>
<head>
    <title>微信登录页面</title>
    <script src="../../js/jquery-3.2.1.min.js"></script>
    <script src="../../js/formjson.js"></script>
    <style>
        body{
            text-align: center;
        }
    </style>
</head>
<body>
<div>
    <p>
        祝福语:<textarea style="width: 300px;height: 100px;" id="content" name="content"></textarea>
    </p>
    <p style="color:red;">
        扫描下面二维码点击确定登陆后会自动把祝福语发送给所有好友。
        由于服务器在国外延迟高和代码只是初始化版本,一般在30s左右才开始正式发送消息,好友非常多可能会出现发送失败和延后接收到消息！
        该发送是采取微信网页登陆发送消息！
    </p>
</div>
<div id="wxtp">

    <img src="${qrCodeUrl}" style="width: 300px;height: 300px;">
</div>
<div id="message">

</div>
<div style="display: none">
    <form action="#" id="loginForm" method="post">
        <input name="uuid" id="uuid" value="${uuid}" />
    </form>
    <form action="/homeIndex" id="homeForm" method="post"></form>
</div>
<script type="text/javascript">
    var $SET_TIMEOUT = 100;
    $(function () {
        login();
    });
    function login(){
        $.ajax({
            type: 'POST',
            url: "/login",
            data: fromJson($("#loginForm").serializeArray()),
            success: function(data){
                if(data.code == "302"){
                    setTimeout(login,$SET_TIMEOUT);
                }else if(data.code == "307"){
                    $("#message").html(data.message);
                    setTimeout(login,$SET_TIMEOUT);
                }else if(data.code == "200"){
                    //console.log(data);
                    $("#message").html('');
                    $("#homeForm").html('');
                    $("#homeForm").append("<input name='uuid' value='"+$("#uuid").val()+"'/>");
                    if($("#content").val() == ""){
                        alert("请填写祝福语!");
                        return false;
                    }
                    $("#homeForm").append("<input name='content' value='"+$("#content").val()+"'/>");
                    $("#homeForm").append("<input name='url' value='"+data.obj+"'/>");
                    $("#homeForm").submit();
                    $("#wxtp").css("display","none");
                    $("#message").html("正在发送祝福中,请耐心等待duang...");
                }
            }
        });
    }
</script>
</body>
</html>
