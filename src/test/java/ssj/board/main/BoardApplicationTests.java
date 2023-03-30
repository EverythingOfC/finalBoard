package ssj.board.main;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ssj.board.main.entity.Board;
import ssj.board.main.repository.BoardRepository;

@SpringBootTest
class BoardApplicationTests {

	@Autowired
    private BoardRepository boardRepository;

    @Test
    void testJpa() {
        for (int i = 1; i <= 100; i++) {
        	String author = "서성준" + i;
        	String password = "@ssj0"+i;
            String title = "meta" + i;
            String content = "바꿀 예정";
            Integer countS = 1;
            LocalDateTime local = LocalDateTime.now();
            
            Board board = Board.builder().
            		author(author).
            		password(password).
            		title(title).
            		content(content).
            		countS(countS).
            		writeDate(local).build();
            		
            this.boardRepository.save(board);
        }
    }

}
