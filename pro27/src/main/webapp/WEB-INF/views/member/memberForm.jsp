<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<%
request.setCharacterEncoding("UTF-8");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원 가입창</title>
<script src="http://code.jquery.com/jquery-latest.js"></script>
<script>
var cnt=1;
function fn_addFile(){
	$("#d_file").append("<br>"+"<input  type='file' name='image"+cnt+"' />");
	cnt++;
}
</script>
<style>
.text_center {
	text-align: center;
}
</style>
</head>
<body>
	<!-- enctype="multipart/form-data" 파일이 포함된 폼데이터 전송시 사용 -->

	<form method="post" action="${contextPath}/member/addMember.do" enctype="multipart/form-data">
		<h1 class="text_center">회원 가입창</h1>
		<table align="center">
			<tr>
				<td width="200"><p align="right">아이디</td>
				<td width="400"><input type="text" name="id"></td>
			</tr>
			<tr>
				<td width="200"><p align="right">비밀번호</td>
				<td width="400"><input type="password" name="pwd"></td>
			</tr>
			<tr>
				<td width="200"><p align="right">이름</td>
				<td width="400"><p>
						<input type="text" name="name"></td>
			</tr>
			<tr>
				<td width="200"><p align="right">이메일</td>
				<td width="400"><p>
						<input type="text" name="email"></td>
			</tr>
			<tr>
				<td width="200"><p align="right"></td>
				<td width="400">
					<input type="button"  value="파일추가" onClick="fn_addFile()"/><br>
					<div id="d_file"></div>
 		 		</td>
			</tr>
			<tr>
				<td width="200"><p>&nbsp;</p></td>
				<td width="400"><input type="submit" value="가입하기"><input
					type="reset" value="다시입력"></td>
			</tr>
		</table>
	</form>
</body>