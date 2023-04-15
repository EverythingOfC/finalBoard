package ssj.board.main;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ssj.board.main.dto.BoardDto;
import ssj.board.main.repository.BoardRepository;

@SpringBootTest
class BoardApplicationTests {

	@Autowired
    private BoardRepository boardRepository;

    @Test
    void testJpa() {
    	
    	
    	for(int i=0;i<100;i++) {
    		BoardDto boardDto = BoardDto.builder().author("서성준00"+i).password(String.valueOf(i)).title("원글 테스트"+i).content("내용"+i)
    				.writeDate(LocalDateTime.now()).orNo(0).grOr(1).grDepth(1).build();		// 얻어온 값으로 초기화
    		
    		BoardDto dto = this.boardRepository.save(boardDto.toEntity()).toDto();
    		
    		dto.setOrNo(dto.getNo());	// 게시글 고유 번호로 그룹 번호를 초기화
			
    		this.boardRepository.save(dto.toEntity());
    	}
    }

}
