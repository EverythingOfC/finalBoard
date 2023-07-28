package ssj.board.main.dto;

import java.time.LocalDateTime;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ssj.board.main.entity.Board;
import ssj.board.main.entity.Comment;

@Data
@NoArgsConstructor
public class CommentDto {

	private Integer coNo;	// 댓글
	
	private String author;	// 작성자	(JPA에서 String의 길이는 default로 255이다.)
	
	private String password;// 비밀번호
	
	private String content;	// 내용
	
	private LocalDateTime writeDate;	// 작성일자
	
	private Board board;	// 해당 게시글의 댓글

	@Builder
	public CommentDto(Integer coNo, String author, String password, String content, LocalDateTime writeDate,
			Board board) {
		super();
		this.coNo = coNo;
		this.author = author;
		this.password = password;
		this.content = content;
		this.writeDate = writeDate;
		this.board = board;
	}
	
	public Comment toEntity() {
		Comment comment = Comment.builder().coNo(coNo)
						.author(author)
						.password(password)
						.content(content)
						.writeDate(writeDate)
						.board(board).build();
		return comment;
	}
	
	public void commentUpdate(String author, String password, String content, LocalDateTime writeDate, Board board) {
		this.author = author;
		this.password = password;
		this.content = content;
		this.writeDate = writeDate;
		this.board = board;
	}
	
}
