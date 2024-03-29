
function filesView(size,no){   // 파일 이미지나 개수 클릭 시 동작
    const removeUl = document.querySelector('td span ul');
    if(removeUl != null){
        const rId = removeUl.id;
        removeUl.remove();      // 이전에 클릭된 게시글의 목록을 지움.
        if(rId == 'uList'+no)   // 현재 클릭된 게시글의 파일이라면 지우고 종료
            return;
    }

    const dropBlock = document.getElementById('dropBlock'+no);
    const a = document.createElement('a');
    const compress = document.createElement('a');

    a.textContent = "X"
    a.style.cursor = "pointer";
    a.style.fontSize = '12px';
    a.style.color = 'grey';
    a.style.display = 'inline-block';
    a.style.marginBottom = '10px';

    compress.text='압축 다운로드';
    compress.style.color = 'blue';
    compress.style.display = 'inline-block';
    compress.style.fontSize = '11px';
    compress.style.marginBottom = '10px';
    compress.href="/downloadZip?no="+no;
    compress.style.float='right';


    a.onclick = function (){
        document.querySelector('#dropBlock'+no + ' ul').remove();
    }

    let ul = document.createElement('ul');
    ul.appendChild(a);
    ul.appendChild(compress);
    ul.appendChild(document.createElement('p'));

    for(let i=0;i<size;i++){    // size가 문자열이더라도 숫자로 파싱 가능한 형태이면 숫자로 간주
        const li = document.createElement('li');
        li.style.display='block';
        li.style.textAlign = 'left';
        const a = document.createElement('a');
        const text = document.createTextNode('\u00a0\u00a0\u00a0\u00a0');
        const size = document.createElement('span');
        const download = document.createElement('span');
        const fileName = document.getElementById('fileName'+i).value;
        const savedPath = document.getElementById('filePath'+i).value;
        const fileSize = document.getElementById('fileSize'+i).value;
        a.style.fontSize = '10px';
        a.text = `${i+1}. ` + fileName;
        a.href = "/download?savedPath="+savedPath;
        download.style.color='red';
        download.textContent = ' [다운로드]';
        download.style.fontSize = '9px';
        a.appendChild(download);
        size.style.fontSize = '10px';
        size.style.display = 'inline-block';
        size.style.float = 'right';
        size.textContent = (parseInt(fileSize)/(1024)).toFixed(2) + ' KB';
        li.appendChild(a);
        li.appendChild(text);
        li.appendChild(size);
        ul.appendChild(li);
    }

    ul.id='uList'+no;
    ul.style.position = 'absolute';
    ul.style.top='110%';
    ul.style.left='0';
    ul.style.zIndex = '10';
    ul.style.boxShadow = '0 1px 12px 0 rgba(0,0,0,.06)';
    ul.style.backgroundColor = '#fff';
    ul.style.padding = '4px 10px 8px 10px';
    ul.style.width='320px';
    ul.style.textAlign = 'left';
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
