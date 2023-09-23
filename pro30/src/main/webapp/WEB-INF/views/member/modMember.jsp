<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<%
request.setCharacterEncoding("UTF-8");
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원 정보 수정창</title>
<script>
	/* 이미지를 뷰에 출력하기 위한 함수 */
	function readURL(input) {
		if (input.files && input.files[0]) {
			var reader = new FileReader();
			reader.onload = function(e) {
				$('#preview').attr('src', e.target.result);
			}
			reader.readAsDataURL(input.files[0]);
		}
	}
</script>
<style>
.text_center {
	text-align: center;
}

#preview {
	width: 50%
}
</style>
</head>
<body>
	<form method="post" action="${contextPath}/member/updateMember.do">
		<h1 class="text_center">회원 정보 수정창</h1>
		<table align="center">

			<tr>
				<td colspan="2" width="400"><img
					src="${contextPath}/memberImage_download.do?image=${member.image}"
					id="preview" /><br></td>
			</tr>
			<tr>
				<td width="200"><p align="right">아이디</td>
				<td width="400"><input type="text" name="id"
					value=${user_id
					} readOnly></td>
			</tr>
			<tr>
				<td width="200"><p align="right">비밀번호</td>
				<td width="400"><input type="password" name="pwd"
					value=${member.pwd}></td>
			</tr>
			<tr>
				<td width="200"><p align="right">이름</td>
				<td width="400"><p>
						<input type="text" name="name" value=${member.name}></td>
			</tr>
			<tr>
				<td width="200"><p align="right">이메일</td>
				<td width="400"><p>
						<input type="text" name="email" value=${member.email}></td>
			</tr>
			<tr>
				<td width="200"><p>&nbsp;</p></td>
				<td width="400"><input type="submit" value="수정하기"><input
					type="reset" value="다시입력"></td>
			</tr>
		</table>
	</form>
</body>
</html>