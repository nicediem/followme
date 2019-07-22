<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>

<html>
<script src="//code.jquery.com/jquery.min.js"></script>
<script type="text/javascript">

var dupChk = 0;
var total_count = 0;
var pageable_count = 0;
var is_end  = false;
var page = 1;
var currentPage = 1;
var pageClick = 0;

$(document).ready(function(){
	$("#booktitle").keydown(function(key) {
        if (key.keyCode == 13) {
            $('#searchBtn').trigger('click');
        }
    });
	
	function callSearchKeyWord(){
		$.ajax({
			url:'./searchKeyWord.do'
		    ,type:'post'
		    ,dataType:'json'
		    ,async:true
		    ,success:function(data){
		    	$.each(data, function(key,value){
			    	if(key== "mySearchHistList"){
			    		var docHtml = "<span style='color:#8637a0; font-weight:bolder;'>";
			    		$.each(value,function(key,value){
			    			docHtml += "#" + value.keyWord + " ";
			    		});
			    		docHtml += "</span>"
			    		$('.mySearchKeyWordList').html(docHtml);
				    }

					if(key== "searchKeyWordList"){
						var docHtml = "<span style='color:#8637a0; font-weight:bolder;'>";
						$.each(value,function(key,value){
							docHtml += "#" + value.keyWord + " " + value.cnt +"회 ";	
			    		});
						docHtml += "</span>"
						$('.searchKeyWordList').html(docHtml);
				    }
		    	});
		    },error:function(request,error){
				   alert("도서 검색 실패");
				   console.log("code=" + request.status + " error="+error);
			}
		});
	}	
	callSearchKeyWord();

	function paging(total_count,dataPerPage,pageCount,currentPage){
		var totalPage = Math.ceil(total_count/dataPerPage);
		var pageGroup = Math.ceil(currentPage/pageCount);

		var last = pageGroup * pageCount;
		if(last > totalPage){
			last = totalPage;
		}

		var first = last - (pageCount -1 );
		var next = last + 1;
		var prev = first - 1;

		var pagingView = $("#paging");
		var html = "";

		if( prev > 0 ){
			html += "<a href='#' id='prev'><</a>";
		}

		for(var i=first ; i <= last ; i++){
			html += "<a href='#' id=" + i + ">" + i + "</a>";
		}

		if( last < totalPage ){
			html += "<a href='#' id='next'>></a>";
		}

		$("#paging").html(html);
		$("#paging a").css("color","black");
		$("#paging a#" + currentPage).css({"text-decoration":"none","color":"red","font-weight":"bold"});

		$("#paging a").click(function(){
			var $item = $(this);
			var $id = $item.attr("id");
			var selectedPage = $item.text();

			if($id == "next") selectedPage = next;
			if($id == "prev") selectedPage = prev;

			page = selectedPage;
			pageClick = 1;
			$('#searchBtn').trigger("click");
		});
	}
	
	function isEmpty(str){
		if(typeof str == "undefined" || str == null || str == ""){
			return true;
		}else{
			return false;
		}
	}
	
	$('#searchBtn').click(function(){

		if(isEmpty($("#booktitle").val())){
			alert("책 제목을 입력해주세요.");
			return ;
		}


		if( dupChk != 0){
			return ;
		}
		dupChk = 1;
		
		var param = {"title":$("#booktitle").val(),"page":page,"pageClick":pageClick};
		
		$.ajax({
			url:'./search.do'
		    ,type:'post'
		    ,data:param
		    ,dataType:'json'
		    ,async:true
		    ,success:function(data){
		    	dupChk = 0;
		    	pageClick = 0;
		    	
			    if(data.err == "true"){
			    	alert(data.msg);
				}else{					
					total_count    = data.total_count;
					is_end         = data.is_end;

					$(".searchList").html("");
					$("#paging").html("");
					
					// 페이징 처리 하기
					if(is_end == "false"){
						// 10개씩 총 10페이지로보여준다.
						var dataPerPage = 10;
						var pageCount = 10;
						var currentPage = page;
						paging(total_count,dataPerPage,pageCount,currentPage);
					}
					
					$.each(data, function(key,value){
						console.log(key + " :: " + value);
						if(key== "mySearchHistList"){
				    		var docHtml = "<span style='color:#8637a0; font-weight:bolder;'>";
				    		$.each(value,function(key,value){
				    			docHtml += "#" + value.keyWord + " ";
				    		});
				    		docHtml += "</span>"
				    		$('.mySearchKeyWordList').html(docHtml);
					    }
					    
						if(key == "documentList"){	
							var docHtml = "";
							var seq = 1;		
							$.each(value,function(key,value){
								// 검색 리스트 테이블로 붙이기
								docHtml += "<div data-seq='" + seq + "' >";
								docHtml += "<img id='img' src=" + value.thumbnail + " width='100px' height='100px'";
								docHtml += "<span id='title'>제목 : "+ value.title + " </span>";
								docHtml += "<span style='display:none;'>저자 : "+ value.authors + "<br></span>"; 
								docHtml += "<span style='display:none;'>소개 : "+ value.contents + "<br></span>";
								docHtml += "<span style='display:none;'>출판날짜 : "+ value.datetime + "<br></span>";
								docHtml += "<span style='display:none;'>도서번호 : "+ value.isbn + "<br></span>";
								docHtml += "<span style='display:none;'>정가 : "+ value.price + "<br></span>";
								docHtml += "<span style='display:none;'>출판사 : "+ value.publisher + "<br></span>";
								docHtml += "<span style='display:none;'>판매가 : "+ value.sale_price + "<br></span>";
								docHtml += "<span style='display:none;'>상태정보 : "+ value.status + "<br></span>";
								docHtml += "<span style='display:none;'>번역자 : "+ value.translators + "<br></span>";
								docHtml += "<span style='display:none;'>상세URL : "+ value.url + "<br></span>";
								docHtml += "</div><br>";
								seq++;
							});
							$('.searchList').html(docHtml);
							
						}
					});

				}
		   },error:function(request,error){
			   dupChk = 0;
			   pageClick = 0;
			   alert("도서 검색 실패");
			   console.log("code=" + request.status + " error="+error);
		   }
		});
	});

	$('.searchList').on("click","div",function(){
		$(this).find('span').each(function(){
			$(this).show();	
		});
	});
})
</script>
<head>
	<title>followme</title>
<style>
#booktitle {
    position: relative;
    height: 50px;
    margin: 0 0 14px;
    padding: 10px 100px 10px 15px;
    border: solid 1px #dadada;
    background: #fff;
}
#searchBtn {
    position: relative;
    height: 50px;
    border: solid 1px #dadada;
    background-color: #8637a0;
    color:white;
    width: 60px;
}
</style>
</head>
<body>
	<div style="text-align:center;">
		<div style="background:black; width:350px; display:inline-block; margin:50px;">
			<h1 style="text-align:center; color:white;">도서검색</h1>
		</div>
	</div>



	<div class="search" style="text-align:center;">
	   <input type="text" name="booktitle" id="booktitle" placeholder="검색하실 도서를 입력해주세요.">
	   <input type="button" name="bookSearch" id="searchBtn" value="검색">
	</div>
	
	<br>
	
	<h3>내 검색 히스토리</h3>
	<div class="mySearchKeyWordList">
	</div>
	
	<br>
	
	<h3>인기 키워드</h3>
	<div class="searchKeyWordList">
	</div>
	
	<br>
	
	<h3>검색 결과(※ 제목을 클릭하면 상세를 볼 수 있습니다)</h3>
	<div class="searchList">		
	</div>
	
	<br>
	
	<h3>페이징 리스트</h3>
	<div id="paging">
	</div>
</body>
</html>
