<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>

<html>
<script src="//code.jquery.com/jquery.min.js"></script>

<script type="text/javascript">
var dupChk = 0;
$(document).ready(function(){
	$("#id").keydown(function(key) {
        if (key.keyCode == 13) {
            $('#loginBtn').trigger('click');
        }
    });

	$("#pass").keydown(function(key) {
        if (key.keyCode == 13) {
        	$('#loginBtn').trigger('click');
        }
    });
	
	function isEmpty(str){
		if(typeof str == "undefined" || str == null || str == ""){
			return true;
		}else{
			return false;
		}
	}
	
	$('#loginBtn').click(function(){
		if(isEmpty($("#id").val())){
			alert("ID를 입력해주세요.");
			return ;
		}

		if(isEmpty($("#pass").val())){
			alert("비밀번호를 입력해주세요.");
			return ;
		}

		if( dupChk != 0){
			return ;
		}
		dupChk = 1;
		
		var param = {"id":$("#id").val(),"pwd":$("#pass").val()};
		
		$.ajax({
			url:'./login.do'
		    ,type:'post'
		    ,data:param
		    ,dataType:'json'
		    ,async:true
		    ,success:function(data){
		    	dupChk = 0;
			    if(data.err == "true"){
			    	alert(data.msg);
				}else{
					alert(data.msg);
			    	location.href="/followme/main";
				}
		   },error:function(request,error){
			   dupChk = 0;
			   alert("로그인실패");
			   console.log("code=" + request.status + " error="+error);
		   }
		});
	});
});
</script>
<head>
	<title>followme</title>
<style>
#id,#pass {
    position: relative;
    height: 50px;
    margin: 0 0 14px;
    padding: 10px 100px 10px 15px;
    border: solid 1px #dadada;
    background: #fff;
}

#loginBtn {
	width:200px;
	margin:10px;
	border : none;
    background-color: #8637a0;
    text-align: center;
    font-size: 20px;
    font-weight: 700;
    line-height: 61px;
    color:white;
}
a:link { text-decoration: none; }
</style>	
</head>
<body>
<div id="logo" class="show-logo" style="margin:80px; padding:20px; text-align:center;"> 
	<div id="logo-container" style="background:black; width:350px; display:inline-block; margin:50px;">
		<h1 style="text-align:center; color:white;">FOLLOW ME</h1>
	</div><br>

	<input type="text" name="user" id="id" placeholder="아이디"><br>
   	<input type="password" name="pass" id="pass" placeholder="패스워드"><br>
   	<input type="button" name="login" id="loginBtn" value="로그인"><br>
  	 
   	<div class="login-help">
   		<a href="/followme/join">회원가입</a>
  	</div>
</div>
</body>
</html>
