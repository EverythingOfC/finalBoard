
function filesView(size,no){   // 파일 이미지, 갯수 클릭 시 동작


    const removeUl = document.querySelector('td span ul');
    if(removeUl != null){
        const rId = removeUl.id;
        removeUl.remove();      // 이전에 클릭된 게시글의 목록을 지움.
        if(rId == 'uList'+no)   // 현재 클릭된 게시글의 파일이라면 지우고 종료
            return;
    }

    size = parseInt(size);
    const dropBlock = document.getElementById('dropBlock'+no);
    let ul = document.createElement('ul');

    for(let i=0;i<size;i++){
        const li = document.createElement('li');
        li.style.display='block';
        li.style.textAlign = 'left';
        const a = document.createElement('a');
        const span = document.createElement('span');
        const fileName = document.getElementById('fileName'+i).value;
        const savedPath = document.getElementById('filePath'+i).value;
        const fileSize = document.getElementById('fileSize'+i).value;
        a.style.fontSize = '10px';
        a.text = `${i+1}. ` + fileName;
        a.href = "/download?savedPath="+savedPath;
        span.style.fontSize = '10px';
        span.style.display = 'inline-block';
        span.style.float = 'right';
        span.textContent = (parseInt(fileSize)/(1024)).toFixed(2) + ' KB';
        li.appendChild(a);
        li.appendChild(span);
        ul.appendChild(li);
    }

    ul.id='uList'+no;
    ul.style.position = 'absolute';
    ul.style.top='110%';
    ul.style.left='0';
    ul.style.zIndex = '10';
    ul.style.boxShadow = '0 1px 12px 0 rgba(0,0,0,.06)';
    ul.style.backgroundColor = '#fff';
    ul.style.padding = '8px 10px';
    ul.style.width='300px';
    ul.style.border = '1px solid rgba(0,0,0,.06)';
    ul.style.borderRadius = '6px';

    dropBlock.appendChild(ul);
}

function handleRecommend(){	// 추천 확인

        const lastRecommend = document.getElementById('lastRecommend').value;
        let currentTime = new Date().getTime();

        if(lastRecommend==null || currentTime-lastRecommend > 600000){   // 10분이 지난 후에는 추천 가능한 상태

            if(confirm('추천하시겠습니까?')){
                const no = document.getElementById('no').value;
                const listPage = document.getElementById('listPage').value;

                location.href= "/board/recommend?no="+no + "&listPage="+listPage +"&timeR="+currentTime;
                alert('추천되었습니다.');
            }
        }else	// 마지막 추천을 누르고 1분 전에는 추천 불가능
            alert('일정 시간 내에 다시 추천할 수 없습니다.')

}

function pwCheckC(obj) { // 댓글 수정 삭제

    const coNo = document.getElementById('coNo').value;	// 댓글 번호 값
    const no = document.getElementById('no').value;
    const page = document.getElementById('listPage').value;

    const passwd = document.getElementById('cPass').value; // 비밀번호 값

    while (true) {
        const inp = prompt('비밀번호 확인.');

        if (inp === passwd) { // 일치하면
            if (obj == 'updateC'){
                location.href = '/board/cUpdateForm?no=' + no
                    + '&listPage=' + page + "&coNo=" + coNo;
            }
            else {
                if (confirm('삭제하시겠습니까?')) {
                    alert('삭제되었습니다.');
                    location.href = '/board/cDelete?no=' + no + '&coNo='
                        + coNo + '&listPage=' + page; // 삭제 후 view로
                }
            }
            break;
        } else if (inp == null) { // 사용자가 취소를 눌렀을 때
            break;
        } else {
            alert("비밀번호가 일치하지 않습니다.");
        }
    }
}
