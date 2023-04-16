function check(obj, max) {
	var str = obj.value;	// 이벤트가 일어난 요소의 값
	var str_length = str.length;

	var m_length = max;		// 제한할 길이
	var i = 0;
	var k_byte = 0;
	// var li_len = 0;
	// var one_char = "";		// 한글자씩 검사
	var exceed = "";	// 글자 수 초과시 그 전까지만

	for (i = 0; i < str_length; i++) {
		one_char = str.charAt(i);	// 한글자 추출
		k_byte++;	// 글자 수 증가
	}

	if (k_byte >= m_length) {	// 항목마다 지정할 최대길이는 상이
		alert((m_length - 1) + " 글자까지 입력 가능합니다. \n 이후 글자는 삭제됩니다.");
		exceed = str.substr(0, max - 1);
		obj.value = exceed;	// 초과되기 전까지의 값들을 저장
	}
	obj.focus();
}

function checkSize(obj) {	// 파일 용량 제한(등록 시)
	var fileSizeLimit = 30 * 1024 * 1024; // 30MB
	var files = obj.files;
	var fileSize = 0;	// 합

	for (var i = 0; i < files.length; i++) {
		fileSize += files[i].size;
		if (fileSize > fileSizeLimit) {
			alert("최대 30MB까지 업로드 가능합니다.");
			obj.value = '';
			return false;
		}
	}
	return true;
}

function updateSize(obj) {	// 파일 용량 제한(수정 시)
	var fileSizeLimit = 30 * 1024 * 1024; // 30MB
	const oSize = document.getElementById('oSize').value;	// 기존 업로드된 파일의 크기를 얻어옴.
	
	var files = obj.files;
	var fileSize = parseInt(oSize);		// 기존 업로드된 파일의 크기를 저장

	for (var i = 0; i < files.length; i++) {
		fileSize += files[i].size;
		if (fileSize > fileSizeLimit) {
			alert("최대 30MB까지 업로드 가능합니다.");
			obj.value = '';
			return false;
		}
	}
	return true;
}

function dFile(obj,fId,fSize) {	// 확인을 누르면 해당 파일을 감춤. 용량도 감소해야함.

	if (confirm('삭제하시겠습니까?')) {
		const d = document.getElementById(obj);	// 고유 번호로 span태그를 얻어옴
		d.style.display = 'none';	// 해당 파일 이름을 감춤.

		const dFile = document.getElementById("dFile");	// input type hidden의 속성을 받아옴. 
		dFile.value += fId + " ";	// 파일명 추가
		
		const oSize = document.getElementById("oSize");	// 기존의 파일 사이즈가 저장된 필드
		
		oSize.value -= parseInt(fSize);	// 용량 감소

		return true;
	}

	return false;
}

function init(size) {	// 초기화버튼 클릭시 숨겨진 태그와 hidden태그, 파일 용량을 원상 복구
	
	const td = document.querySelectorAll("#fList span");
	const dFile = document.getElementById("dFile");	// input type hidden의 속성을 받아옴. 
	const oSize = document.getElementById("oSize");	// 기존의 파일 사이즈가 저장된 필드
	
	for (var i=0;i<td.length;i++) {
		td[i].style.display = 'inline';
	}

	dFile.value = "";	// 삭제할 fID를 모두 없앰.	
	oSize.value = size;	// 서버에서 가져온 기존의 용량으로 할당
}


