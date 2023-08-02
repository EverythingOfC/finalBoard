function snsShare(){    // 공유하기

    const removeP = document.querySelector('#tdShare p');
    if(removeP!=null){
        removeP.remove();
        return;
    }

    const tdShare = document.getElementById('tdShare');

    const a = document.createElement('a');
    a.textContent = "X"
    a.style.cursor = "pointer";
    a.style.fontSize = '14px';
    a.style.zIndex = '2';
    a.style.color = 'grey';
    a.onclick = function (){
        document.querySelector('#tdShare p').remove();
    }
    const p = document.createElement('p');
    p.appendChild(a);
    p.appendChild(document.createElement('br'));

    p.style.position = 'absolute';
    p.style.top='91%';
    p.style.left='51%';
    p.style.zIndex = '1';
    p.style.boxShadow = '0 1px 12px 0 rgba(0,0,0,.06)';
    p.style.backgroundColor = '#fff';
    p.style.padding = '5px 10px';
    p.style.width='200px';
    p.style.textAlign = 'left';
    p.style.border = '2px solid rgba(0,0,0,.06)';
    p.style.borderRadius = '8px';
    p.style.lineHeight = '20px';
    p.style.cursor = 'default';

    p.appendChild(twitter());
    p.appendChild(document.createTextNode('\u00a0\u00a0'));
    p.appendChild(facebook());
    p.appendChild(document.createTextNode('\u00a0\u00a0'));
    p.appendChild(band());

    tdShare.appendChild(p);
}

function facebook() {
    const facebook = document.createElement('img');

    facebook.src = '../image/icon-facebook.png';
    facebook.width = 40;
    facebook.style.marginTop = '6px';
    facebook.style.verticalAlign = 'middle';
    facebook.style.cursor = 'pointer';
    facebook.addEventListener('click', function () {
        const sendUrl = encodeURIComponent("tkflwk123.tistory.com");
        window.open("http://www.facebook.com/sharer/sharer.php?u=" + sendUrl);
    });
    return facebook;
}

function band() {
    const band = document.createElement('img');

    band.src = '../image/icon-band.png';
    band.width = 40;
    band.style.marginTop = '6px';
    band.style.verticalAlign = 'middle';
    band.style.cursor = 'pointer';
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
    twitter.width = 40;
    twitter.style.marginTop = '6px';
    twitter.style.verticalAlign = 'middle';
    twitter.style.cursor = 'pointer';
    twitter.addEventListener('click', function () {
        const sendUrl = encodeURIComponent("tkflwk123.tistory.com");
        window.open("https://twitter.com/intent/tweet?url=" + sendUrl);
    });
    return twitter;
}
