function snsShare(){    // 공유하기

    const removeP = document.querySelector('#tdShare p');
    if(removeP!=null){
        removeP.remove();
        return;
    }

    const tdShare = document.getElementById('tdShare');

    const a = document.createElement('a');
    a.textContent = "X"
    a.onclick = function (){
        document.querySelector('#tdShare p').remove();
    }
    const p = document.createElement('p');  // X버튼과 이미지를 담는 태그
    p.appendChild(a);
    p.appendChild(document.createElement('br'));

    p.id = 'shareP';

    p.appendChild(twitter());
    p.appendChild(document.createTextNode('\u00a0\u00a0'));
    p.appendChild(facebook());
    p.appendChild(document.createTextNode('\u00a0\u00a0'));
    p.appendChild(band());
    p.appendChild(document.createTextNode('\u00a0\u00a0'));
    p.appendChild(kakao());

    tdShare.appendChild(p);
}

function kakao(){   // 카카오톡 공유 API(카카오 Developer 사이트에 웹/앱 등록 -> SDK 생성 -> 키 초기화 -> 버튼 생성)

    const kakao = document.createElement('img');

    kakao.src = '../image/kakaotalk.ico';
    kakao.addEventListener('click', function () {
        Kakao.init("e39e9393cec6331eb25d65f6359dc812"); // javascript 키로 초기화
        Kakao.Share.sendDefault({
            objectType: 'feed',
            content: {
                title: '게시판 사이트',
                description: '#Java #Back #Front #CRUD #게시판 #다양한 기능',
                imageUrl:
                    '../image/board.png',
                link: {
                    // [내 애플리케이션] > [플랫폼] 에서 등록한 사이트 도메인과 일치해야 함
                    mobileWebUrl: 'http://localhost:8082',
                    webUrl: 'http://localhost:8082',
                },
            },
            social: {
                likeCount: 0,
                commentCount: 0,
                sharedCount: 0,
            },
            buttons: [
                {
                    title: '웹으로 보기',
                    link: {
                        mobileWebUrl: 'http://localhost:8082',
                        webUrl: 'http://localhost:8082',
                    },
                },
                {
                    title: '앱으로 보기',
                    link: {
                        mobileWebUrl: 'http://localhost:8082',
                        webUrl: 'http://localhost:8082',
                    },
                },
            ],
        });
    });

    return kakao;
}

function facebook() {
    const facebook = document.createElement('img');

    facebook.src = '../image/icon-facebook.png';
    facebook.addEventListener('click', function () {
        const sendUrl = encodeURIComponent("tkflwk123.tistory.com");
        window.open("http://www.facebook.com/sharer/sharer.php?u=" + sendUrl);
    });
    return facebook;
}

function band() {
    const band = document.createElement('img');

    band.src = '../image/icon-band.png';
    band.addEventListener('click', function () {
        const sendUrl = "http://www.band.us/plugin/share?body=" +
            encodeURIComponent('board') + "&route=" + encodeURIComponent(document.URL);
        window.open(sendUrl);
    });
    return band;
}
function twitter() {
    const twitter = document.createElement('img');

    twitter.src = '../image/icon-twitter.png';
    twitter.addEventListener('click', function () {
        const sendUrl = encodeURIComponent("tkflwk123.tistory.com");
        window.open("https://twitter.com/intent/tweet?url=" + sendUrl);
    });
    return twitter;
}
