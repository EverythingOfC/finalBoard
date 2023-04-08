package ssj.board.main.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ssj.board.main.entity.Board;
import ssj.board.main.entity.Comment;

@Data
@NoArgsConstructor
public class BoardDto {

	
	private Integer no; // 게시글 고유 번호

	private String author; // 작성자

	private String password;// 비밀번호

	private String title; // 제목

	private String content; // 내용

	private LocalDateTime writeDate; // 작성일자
	
	private List<Comment> cList;	// 댓글 목록
	
	private Integer orNo;	// 원글 번호
	
	private Integer grOr;	// (원글 과 답글)그룹 순서
	
	private Integer grDepth; // 답글 깊이
	

	public Board toEntity() {
		Board board = Board.builder().no(no).author(author).password(password).title(title).cList(cList)
				.content(content).writeDate(writeDate).orNo(orNo).grOr(grOr).grDepth(grDepth).build();
		return board;
	}

	@Builder
	public BoardDto(Integer no, String author, String password, String title, String content, LocalDateTime writeDate,
			List<Comment> cList, Integer orNo, Integer grOr, Integer grDepth) {
		super();
		this.no = no;
		this.author = author;
		this.password = password;
		this.title = title;
		this.content = content;
		this.writeDate = writeDate;
		this.cList = cList;
		this.orNo = orNo;
		this.grOr = grOr;
		this.grDepth = grDepth;
	}
	
	public void boardUpdate(String author, String password, String title, String content, LocalDateTime writeDate) {
		this.author = author;
		this.password = password;
		this.title = title;
		this.content = content;
		this.writeDate = writeDate;
	}

}
