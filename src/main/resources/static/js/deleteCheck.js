function Board(){
	
	if(document.getElementById("result").value == "delete"){	// 삭제인 경우
		if(confirm('삭제하시겠습니까?'))
			document.check.submit();
		else{
			document.getElementById("result").focus();
			return false;
		}
	}
	else
		document.check.submit();	// 수정인 경우(수정 폼으로 이동)
}

