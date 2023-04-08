function check(obj, max){
	var str = obj.value;	// 이벤트가 일어난 요소의 값
	var str_length = str.length;
	
	var m_length = max;		// 제한할 길이
	var i = 0;
	var k_byte = 0;
	// var li_len = 0;
	// var one_char = "";		// 한글자씩 검사
	var exceed = "";	// 글자 수 초과시 그 전까지만
	
	for(i=0;i<str_length;i++){
		one_char = str.charAt(i);	// 한글자 추출
		k_byte++;	// 글자 수 증가
	}
	
	if(k_byte >= m_length){	// 항목마다 지정할 최대길이는 상이
		alert((m_length-1)+ " 글자까지 입력 가능합니다. \n 이후 글자는 삭제됩니다.");
        exceed = str.substr(0, max-1);
        obj.value = exceed;	// 초과되기 전까지의 값들을 저장
	}
	obj.focus();
}
