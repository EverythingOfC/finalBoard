
function handleRecommend(){	// localStorage를 통한 브라우저에 key-value 값 저장 ( 추천 체크 )

        let currentTime = new Date().getTime();
        let lastRecommend = localStorage.getItem('lastRecommend');

        if(lastRecommend==null || currentTime-lastRecommend > 60000 && confirm('추천하시겠습니까?')){   // 1분이 지난 후에는 추천 가능한 상태
            const no = document.getElementById('no').value;
            const listPage = document.getElementById('listPage').value;

            localStorage.setItem('lastRecommend',currentTime);
            location.href= "/board/recommend?no="+no + "&listPage"+listPage;

            alert('추천되었습니다.');
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