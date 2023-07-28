package ssj.board.main.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssj.board.main.dto.CommentDto;

@Entity
@Getter
@NoArgsConstructor
public class Comment {	// 댓글

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	// 칼럼만의 독립적인 시퀀스
	private Integer coNo;	// 댓글
	
	@Column(nullable=false)
	private String author;	// 작성자	(JPA에서 String의 길이는 default로 255이다.)
	
	@Column(nullable=false)
	private String password;// 비밀번호
	
	@Column(nullable=false,length = 751)	// 내용은 751
	private String content;	// 내용
	
	@Column(columnDefinition = "DATETIME")  // 8바이트(날짜와 시간을 같이 나타냄)
	private LocalDateTime writeDate;	// 작성일자
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="board_id")
	private Board board;	// 해당 게시글의 댓글

	@Builder
	public Comment(Integer coNo, String author, String password, String content, LocalDateTime writeDate,
			Board board) {
		super();
		this.coNo = coNo;
		this.author = author;
		this.password = password;
		this.content = content;
		this.writeDate = writeDate;
		this.board = board;
	}
	
	public CommentDto toDto() {
		CommentDto commentDto = CommentDto.builder().coNo(coNo)
						.author(author)
						.password(password)
						.content(content)
						.writeDate(writeDate)
						.board(board).build();
		return commentDto;
	}
	
}
