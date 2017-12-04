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
                    console.log(data);
                    $("#message").html('');
                    $("#homeForm").html('');
                    $("#homeForm").append("<input name='uuid' value='"+$("#uuid").val()+"'/>");
                    $("#homeForm").append("<input name='url' value='"+data.obj+"'/>");
                    $("#homeForm").submit();
                }
            }
        });
    }
</script>
</body>
</html>
