package ssj.board.main.service;

public class BadWordImpl implements BadWord{
	
	@Override
	public boolean test(String check) {	// 욕이 없으면 참
		int length = koreaWord1.length;
		check = check.replace(" ","");	// 공백 제거
		
		if(check.length() < 1)	// 공백 뿐이면
			return true;
		
		for(int i=0;i<length;i++) {
			if(check.contains(koreaWord1[i]))	// 욕이 포함되어 있으면
				return false;
		}
		return true;
	}
}
