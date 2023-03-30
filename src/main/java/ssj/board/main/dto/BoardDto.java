package ssj.board.main.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ssj.board.main.entity.Board;

@Data
@NoArgsConstructor
public class BoardDto {

	private Integer no; // 게시글 고유 번호

	private String author; // 작성자

	private String password;// 비밀번호

	private String title; // 제목

	private String content; // 내용

	private LocalDateTime writeDate; // 작성일자

	private Integer countS; // 조회 수

	@Builder
	public BoardDto(Integer no, String author, String password, String title, String content,
			LocalDateTime writeDate, Integer countS) {
		super();
		this.no = no;
		this.author = author;
		this.password = password;
		this.title = title;
		this.content = content;
		this.writeDate = writeDate;
		this.countS = countS;
	}

	public Board toEntity() {
		Board board = Board.builder().no(no).author(author).password(password).title(title)
				.content(content).writeDate(writeDate).countS(countS).build();
		return board;
	}

}
