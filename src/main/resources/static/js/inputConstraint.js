function check(obj, max) {
   var str = obj.value;   // 이벤트가 일어난 요소의 값
   var str_length = str.length;

   var m_length = max;      // 제한할 길이
   var i = 0;
   var k_byte = 0;
   // var li_len = 0;
   // var one_char = "";      // 한글자씩 검사
   var exceed = "";   // 글자 수 초과시 그 전까지만

   for (i = 0; i < str_length; i++) {
      one_char = str.charAt(i);   // 한글자 추출
      k_byte++;   // 글자 수 증가
   }

   if (k_byte >= m_length) {   // 항목마다 지정할 최대길이는 상이
      alert((m_length - 1) + " 글자까지 입력 가능합니다. \n 이후 글자는 삭제됩니다.");
      exceed = str.substr(0, max - 1);
      obj.value = exceed;   // 초과되기 전까지의 값들을 저장
   }
   obj.focus();
}

function checkSize(obj) {   // 파일 용량 제한(등록 시)

   const fileSizeLimit = 30 * 1024 * 1024; // 30MB
   const fileInputs = document.querySelectorAll('input[type="file"]');
   const parent = obj.parentNode;   // 현재 파일 필드의 부모를 선택
   const oSize = document.getElementById('oSize');   // 기존 업로드된 파일의 크기를 가진 요소를 얻어옴.

   let totalSize = 0;    // 전체 파일 필드의 크기
   
   if(oSize)
      totalSize = parseInt(oSize.value);

   const ps = parent.querySelectorAll('p');
   const b = parent.querySelectorAll('br');	
   ps.forEach(p => {
      parent.removeChild(p);
   });
   b.forEach(bb=> {
      parent.removeChild(bb);
   });

   for (let i = 0; i < fileInputs.length; i++) {// 파일 필드 순회

      const files = fileInputs[i].files;        // 사이즈 체크를 위해, 각 파일필드의 모든 파일들을 저장
      const prefileList = fileInputs[i].parentNode;
      

      for (let j = 0; j < files.length; j++) { // 파일 필드의 파일들을 순회
         totalSize += files[j].size;   // 파일의 크기 누적

         if (totalSize > fileSizeLimit) {
            alert('최대 30MB까지 업로드 가능합니다.');
            obj.value = '';
            
            const fileList = obj.parentNode;   // 해당 파일 필드의 부모 요소
            const ps = fileList.querySelectorAll('p');   // 해당 파일 필드의 p태그를 가져옴.
            const b = fileList.querySelectorAll('br');	 // 해당 파일 필드의 br태그를 가져옴.

			console.log(b);
            ps.forEach(p => {
               fileList.removeChild(p);
            });
            b.forEach(bb=> {
               fileList.removeChild(bb);
            });
            return false;
         }
         else if (parent.id == prefileList.id) {   // 해당 파일 필드인 경우에만 표시
         
            const p = document.createElement('p');
            p.innerText = `파일명: ${files[j].name}`;
            prefileList.appendChild(p); // 해당 파일 필드에, 파일명들을 표시
            if(j==files.length-1){   // 마지막일 때 br태그 추가
               const br = document.createElement('br');
               prefileList.appendChild(br);
            }
         }
      }
      
   }
      
   return true;
}

function insertField(obj) {
   // +버튼 클릭 시 파일 필드와 -버튼 추가
   const ff = document.createElement('input');
   const plus = document.createElement('input');
   const minus = document.createElement('input');
   const space = document.createTextNode(' ');
   const i = document.querySelectorAll('input[type="file"]').length;   // 파일필드의 길이만큼 얻어옴.(처음엔 1)

   ff.type = 'file';   // input type file 추가
   ff.name = 'files';
   ff.id = `files-${i}`;
   ff.multiple = 'multiple';
   ff.addEventListener('change', function() {   // change이벤트가 발생했을 때, 실행되는 함수
      checkSize(ff);   
   });

   plus.type = "button";    // +버튼 추가
   plus.style.padding = "0 8px 2px 8px";
   plus.value = "+";
   plus.addEventListener('click', function() {
      insertField(plus);
   });

   minus.type = "button";   // -버튼 추가
   minus.style.padding = "0 9px 2px 9px";
   minus.value = "-";
   minus.addEventListener('click', function() {
      deleteField(minus);
   });

   // 파일 리스트를 표시할 div태그 생성
   const fileListDiv = document.createElement('div');
   fileListDiv.id = `file-list-${i}`;
    const newTd = document.getElementById('fileField');   // 파일리스트의 부모객체
   const parent = obj.parentNode;
   const nextSibling = parent.nextElementSibling;

   if (nextSibling) {  // 다음 요소가 있다면
      newTd.insertBefore(fileListDiv, nextSibling);  // 해당 요소 바로 다음에 추가
   } else {  // 다음 요소가 없다면
      newTd.appendChild(fileListDiv); // 해당 파일 필드와 연결된 파일 리스트를 표시할 div태그 추가
   }   
      fileListDiv.appendChild(plus);
      fileListDiv.appendChild(space);
      fileListDiv.appendChild(minus);
      fileListDiv.appendChild(ff);   // 파일리스트 div 자식에 input type file 생성

}

function deleteField(obj) {   // 필드 삭제 시, 용량도 그만큼 삭제
   const parent = obj.parentNode;   // 파일 필드의 부모인 파일 필드를 감싸는 div를 불러옴.
   
   parent.parentNode.removeChild(parent);   // 불러온 div의 부모에서 파일필드를 감싸는 div자식을 삭제

}


function dFile(obj, fId, fSize) {   // 첨부파일에서 삭제를 누르면 해당 파일을 감춤. 용량도 감소해야함.

   const d = document.getElementById(obj);   // 고유 번호로 span태그를 얻어옴
   d.style.display = 'none';       // 해당 파일 이름을 감춤.

   const dFile = document.getElementById("dFile");   // input type hidden의 속성을 받아옴. 
   const oSize = document.getElementById("oSize");   // 기존의 파일 사이즈가 저장된 필드
   dFile.value += fId + " ";       // 파일 ID 추가
   oSize.value -= parseInt(fSize); // 용량 감소
   
}

function init(size) {   // 수정 페이지에서 재입력 클릭시 숨겨진 태그와 hidden태그, 파일 용량을 원상 복구

   const td = document.querySelectorAll("#fList span");   // 이전에 업로드된 파일들이 있는 태그
   const dFile = document.getElementById("dFile");   		// input type hidden의 속성을 받아옴. 
   const oSize = document.getElementById("oSize");   // 기존의 파일 사이즈가 저장된 필드

   for (var i = 0; i < td.length; i++) {	// 감춰져 있던 td의 요소들을 하나씩 보여지게 함.
      td[i].style.display = 'inline';
   }

   const field = document.getElementById("fileField");
   const div = field.querySelectorAll("div");   // div태그들을 불러옴.

   for(let d=1;d<div.length;d++){   // 두 번째 필드부터 제거 (파일 필드를 담은 div요소)
      div[d].remove();   
   }

   dFile.value = "";   // 삭제할 fID를 모두 없앰.   
   oSize.value = size;   // 서버에서 가져온 기존의 용량으로 할당

}


function initC() {   // 등록 페이지에서 div태그와 파일 용량을 원상 복구
   const field = document.getElementById("fileField");
   const div = field.querySelectorAll("div");   // div태그들을 불러옴.

   for(let d=1;d<div.length;d++){   // 두 번째 필드부터 제거
      div[d].remove();   
   }

}