

function checkPw(pw) {

	let number = pw.search(/[0-9]/g);	// 숫자검사
	let english = pw.search(/[a-z]/ig);	// 영문검사
	let space = pw.search(/[~!@#$%^&*()\-_=\+|<>?:{}\[\]\'\"\;\\\/]/);
	const reg = /([A-Za-z0-9`~!@#\$%\^&\*\(\)\{\}\[\]\-_=\+\\|;:'"<>,\./\?])\1{3,}/g;
	
	if (pw.length < 6 || pw.length > 20) {
		alert("비밀번호를 6자리 ~ 20자리 이내로 입력해주세요.");
		return false;

	} else if (pw.search(/\s/) != -1) {
		alert("비밀번호를 공백 없이 입력해주세요.");
		return false;
	} else if (number < 0 || english < 0 || space < 0) {
		alert("비밀번호는 영문, 숫자, 특수문자가 적어도 1개 이상 포함되어야 합니다.");
		return false;
	}else if (reg.test(pw)) {
		alert('같은 문자를 4번 연속해서 넣을 수 없습니다.');
		return false;
	}/*else if(pw!=pwC){
		alert('비밀번호가 일치하지 않습니다.');
		return false;
	}*/else {
		return true;
	}
}

String.prototype.trim = function() {
	return this.replace(/^\s+|\s+$/g, "");
}

function updateBoard() {	// 게시글 수정
	var author = document.getElementById("author");
	var pw = document.getElementById("password");
	var title = document.getElementById("title");
	var content = document.getElementById("content");
	var pwV = pw.value.trim();
	// var pwC = document.getElementById("passwordC").value.trim();

	if (author.value.trim() == "") {
		alert("작성자를 입력해주세요.");
		author.focus();
		return false;
	}
	
	if (pwV == "") {
		alert("비밀번호를 입력해주세요.");
		pw.focus();
		return false;
	}

	if (!checkPw(pwV)) {	// 비밀번호 유효성 검사
		pw.focus();
		return false;
	}

	if (title.value.trim() == "") {
		alert("제목을 입력하세요.");
		title.focus();
		return false;
	}
	
	if (content.value.trim() == "") {
		alert("내용을 입력하세요.");
		content.focus();
		return false;
	}
	if(confirm('수정하시겠습니까?')){
		alert('수정되었습니다.');	
		document.update.submit();
	}
	else{
		return false;
	}
}

function cUpdateBoard() {	// 게시글 수정
	var author = document.getElementById("author");
	var pw = document.getElementById("password");
	var content = document.getElementById("content");
	var pwV = pw.value.trim();
	// var pwC = document.getElementById("passwordC").value.trim();

	if (author.value.trim() == "") {
		alert("작성자를 입력해주세요.");
		author.focus();
		return false;
	}
	
	if (pwV == "") {
		alert("비밀번호를 입력해주세요.");
		pw.focus();
		return false;
	}

	if (content.value.trim() == "") {
		alert("내용을 입력하세요.");
		content.focus();
		return false;
	}
	if(confirm('수정하시겠습니까?')){
		alert('수정되었습니다.');	
		document.update.submit();
	}
	else{
		return false;
	}
}


function joinBoard() {	// 게시글 생성
	var author = document.getElementById("author");
	var pw = document.getElementById("password");
	var title = document.getElementById("title");
	var content = document.getElementById("content");
	var fileList = document.getElementById("files");
	var pwV = pw.value.trim();
	// var pwC = document.getElementById("passwordC").value.trim();

	if (author.value.trim() == "") {
		alert("작성자를 입력해주세요.");
		author.focus();
		return false;
	}
	
	if (pwV == "") {
		alert("비밀번호를 입력해주세요.");
		pw.focus();
		return false;
	}

	if (!checkPw(pwV)) {	// 비밀번호 유효성 검사
		pw.focus();
		return false;
	}

	if (title.value.trim() == "") {
		alert("제목을 입력하세요.");
		title.focus();
		return false;
	}
	
	if (content.value.trim() == "") {
		alert("내용을 입력하세요.");
		content.focus();
		return false;
	}

	document.create.submit();
}

function cJoinBoard() {	// 게시글 생성
	var author = document.getElementById("cAuthor");
	var pw = document.getElementById("cPassword");
	var content = document.getElementById("cContent");
	var pwV = pw.value.trim();

	if (author.value.trim() == "") {
		alert("작성자를 입력해주세요.");
		author.focus();
		return false;
	}
	
	if (pwV == "") {
		alert("비밀번호를 입력해주세요.");
		pw.focus();
		return false;
	}
	
	if (content.value.trim() == "") {
		alert("내용을 입력하세요.");
		content.focus();
		return false;
	}

	document.create.submit();
}

