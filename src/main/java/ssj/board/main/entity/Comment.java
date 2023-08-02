package ssj.board.main.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssj.board.main.dto.CommentDto;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
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
	private Board board;	// 댓글의 원본 글

	@OneToMany(mappedBy = "comment",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	private List<Report> reports;



	public CommentDto toDto() {
		CommentDto commentDto = CommentDto.builder().coNo(coNo)
						.author(author)
						.password(password)
						.content(content)
						.writeDate(writeDate)
						.board(board)
						.reports(reports)
						.build();
		return commentDto;
	}
	
}
