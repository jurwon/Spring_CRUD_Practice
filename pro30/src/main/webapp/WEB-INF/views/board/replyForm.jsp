<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    isELIgnored="false" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath"  value="${pageContext.request.contextPath}"  />
<c:set var="passwordCheck2" value="${passwordCheck }"/>
<%
  request.setCharacterEncoding("UTF-8");
%> 

<head>
<meta charset="UTF-8">
 <script src="//code.jquery.com/jquery-3.3.1.js"></script> 
<script type="text/javascript">
var checkPwd = 0 

/* cnt , 파일 추가시 , 입력태그 구분 짓는 변수 */
var cnt=1;
/* cnt1 , 이미지 불러오는 태그를 구분 짓는 변수 */
var cnt1=0; 
/* cnt2 img 태그를 구분짓는 변수 */
var cnt2=0;

function readURL2(input) {
	  console.log('readURL2 호출 여부 확인')
  if (input.files && input.files[0]) {
	      var reader = new FileReader();
	     
	      reader.onload = function (e) {
	    	  console.log('preview 호출 전 cnt2 : '+ cnt2)
	        $('#preview'+cnt1).attr('src', e.target.result);
	    	  cnt1++;
	    	  console.log('preview 호출 후 cnt2 : '+ cnt2)
      }
     reader.readAsDataURL(input.files[0]);
  }
} 

function fn_addFile(){
	  $("#d_file").append("<br>"+"<input type='file' name='file"+cnt+"+"+"' onchange="+"readURL2(this); />");
	  
	  cnt++;
	   $("#previews").append("<br>"+"<img id='preview"+cnt2+"' src='#'"+ "width=200 height=200 />");
	   cnt2++;
	  console.log(cnt2);
}  

function checkSubmit() {
	if(checkPwd ==1) {
		document.getElementById('frmReply').submit();
	} else {
		alert("패스워드 확인 해주세요")
		checkPwd = 0
		document.getElementById('inputPassword').focus()
		return false;
	}
}

function passwordConfirm(password){
	if(password == ${member.pwd}){
		checkPwd = 1
		var change = document.getElementById('inputPassword').style.borderColor = "green";
		alert("일치합니다.")
		
	} else {
		document.getElementById('inputPassword').style.borderColor = "red";
		alert("불일치합니다.")
	
	}
	
}

 function backToList(obj){
 obj.action="${contextPath}/board/listArticles.do";
 obj.submit();
 }
</script> 
<title>답글쓰기 페이지</title>
</head>

<body>
 <h1>답글쓰기</h1>
  <form name="frmReply" method="post"  action="${contextPath}/board/addReply.do"   enctype="multipart/form-data">
    <table>
    <tr>
			<td align="right"> 작성자:&nbsp; </td>
			<td><input type="text"  value="${member.id }" name="writer"  readOnly></input> </td>
		</tr>
		<tr>
			<td align="right">제목:&nbsp;  </td>
			<td><input type="text" size="67"  maxlength="500" name="title" > </input></td>
			<td><input type="hidden" size="67"  maxlength="500" name="parentNO" value="${parentNO}" > </input></td>
		</tr>
		<tr>
			<td align="right" valign="top"><br>내용:&nbsp; </td>
			<td><textarea name="content" rows="10" cols="65" maxlength="4000"> </textarea> </td>
		</tr>
		<tr>
			<td align="right">비밀번호:&nbsp;  </td>
			<td><input id ="inputPassword" type="password" size="10" maxlength="12" name="passwd"> </input> </td>
			<td><input type=button value="패스워드확인"onClick="passwordConfirm(inputPassword.value)" /></td>
		</tr>
		<tr>
			  <td align="right">이미지파일 첨부:  </td>
			  <!-- 추가3 -->
			  <td align="left"> <input type="button" value="파일 추가" onClick="fn_addFile()"/></td>
	   </tr>
	    <tr>
	      <td colspan="4"><div id="d_file"></div></td>
	       <td colspan="4"><div id="previews"></div></td>
	   </tr>
	   
		<tr>
			<td align="right"> </td>
			<td>
				<input id="submitBtn" type=submit value="답글쓰기" onClick="return checkSubmit()" />
				<input type=button value="취소"onClick="backToList(this.form)" />
				
			</td>
		</tr>
    
    </table>
  
  </form>
</body>
</html>