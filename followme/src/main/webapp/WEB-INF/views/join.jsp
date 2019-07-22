<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>

<html>
<script src="//code.jquery.com/jquery.min.js"></script>
<script type="text/javascript">

var dupChk = 0;
$(document).ready(function(){
	function isEmpty(str){
		if(typeof str == "undefined" || str == null || str == ""){
			return true;
		}else{
			return false;
		}
	}
		
	$('#joinBtn').click(function(){
		if(isEmpty($("#id").val())){
			alert("아이디를 입력해주세요.");
			return ;
		}

		if(isEmpty($("#pass").val())){
			alert("비밀번호를 입력해주세요.");
			return ;
		}

		if(isEmpty($("#name").val())){
			alert("이름을 입력해주세요.");
			return ;
		}

		if(isEmpty($("#email").val())){
			alert("이메일을 입력해주세요.");
			return ;
		}

		if(!isEmpty($("#email").val())){
			var regExp = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
			if(!regExp.test($("#email").val())){
				alert("이메일형식이 올바르지 않습니다.");
				return ;
			}
		}

		if( dupChk != 0){
			return ;
		}
		dupChk = 1;
		var param = {"id":$("#id").val(),"pwd":$("#pass").val(),"name":$("#name").val(),"email":$("#email").val()};
		$.ajax({
			url:'./joinMember.do'
		    ,data:param
		    ,type:'post'
		    ,dataType:'json'
		    ,async:true
		    ,success:function(data){
		    	dupChk = 0;
		    	if(data.err == "true"){
		    		alert(data.msg);	
			    }else{
			    	alert(data.msg);
			    	location.href="/followme";
				}
		   },error:function(request,error){
			   dupChk = 0;
			   alert("서버점검 중입니다. 잠시 후 다시 시도해주세요");
			   console.log("code=" + request.status + " error="+error);
		   }
		});
	});
});
</script>
<head>
	<title>followme</title>
<style>
#id,#pass,#name,#email {
    position: relative;
    height: 50px;
    margin: 0 0 14px;
    padding: 10px 100px 10px 15px;
    border: solid 1px #dadada;
    background: #fff;
}
#joinBtn {
	width:200px;
	margin:10px;
	border : none;
    background-color: #8637a0;
    color:white;
    text-align: center;
    font-size: 20px;
    font-weight: 700;
    line-height: 61px;
    color:white;
}
</style>
</head>
<body>
<div class='join_cl' style="margin:80px; padding:20px; text-align:center;">
	<div style="background:black; width:350px; display:inline-block; margin:50px;">
		<h1 style="text-align:center; color:white;">회원가입</h1>
	</div>

	<div>
	   <input type="text" name="user" id="id"       placeholder="아이디"><br>
	   <input type="password" name="pass" id="pass" placeholder="패스워드"><br>
	   <input type="text" name="name" id="name"     placeholder="이름"><br>
	   <input type="text" name="email" id="email"   placeholder="이메일"><br>
	   <input type="button" name="joint" class="join join-submit" value="등록" id="joinBtn">
   </div>
</div>
</body>
</html>
