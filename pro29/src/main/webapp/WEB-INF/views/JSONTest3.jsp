<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"  isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"  />
<!DOCTYPE html>
<html>
<head>
<title>JSONTest2</title>
<script src="http://code.jquery.com/jquery-latest.js"></script>  
<script>
$(function() {
	  //아이디 chdckJson 클릭 이벤트가 발생하면 동작하는 함수로 작성
    $("#checkJson").click(function() {
  	  //샘플디비, 더미,js, 객체(인스턴스)는 키와 값의 형태로 구성
    	var member = { id:"park", 
			    name:"박지성",
			    pwd:"1234", 
			    email:"park@test.com" };
  	  // 자바스크립트에서 비동기통신을 하는 것을 말함, 서버데 데이터 비동기 형식으로 전달
	$.ajax({
	  type:"POST",
	  url:"${contextPath}/member2",
      contentType: "application/json",
      data :JSON.stringify(member),
   success:function (data,textStatus){
   },
   error:function(data,textStatus){
      alert("에러가 발생했습니다.");
   },
   complete:function(data,textStatus){
   }
});  //end ajax	

 });
});
</script>
</head>
<body>
  <input type="button" id="checkJson" value="새글 쓰기"/><br><br>
  <div id="output"></div>
</body>
</html>