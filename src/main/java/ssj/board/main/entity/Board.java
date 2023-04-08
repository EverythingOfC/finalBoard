package ssj.board.main.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssj.board.main.dto.BoardDto;

@Getter
@Entity
@NoArgsConstructor
public class Board {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	// 칼럼만의 독립적인 시퀀스
	@Column(name="\"no\"")
	private Integer no;	// 게시글 고유 번호
	
	@Column(nullable=false)
	private String author;	// 작성자	(JPA에서 String의 길이는 default로 255이다.)
	
	@Column(nullable=false)
	private String password;// 비밀번호
	
	@Column(nullable=false)
	private String title;	// 제목
	
	@Column(nullable=false,length = 751)	// 내용은 751
	private String content;	// 내용
	
	@Column(columnDefinition = "DATETIME")  // 8바이트(날짜와 시간을 같이 나타냄)
	private LocalDateTime writeDate;	// 작성일자
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL,mappedBy = "board")	// 연관관계의 주인을 외래키 필드로 함
	private List<Comment> cList;		// 댓글 목록
	
	private Integer orNo;	// 원글의 그룹 번호
	
	private Integer grOr;	// (원글과 답글)그룹 순서
	
	private Integer grDepth;// 답글의 깊이
	
	
	public BoardDto toDto() {
		BoardDto boardDto = BoardDto.builder().no(no)
						.author(author)
						.password(password)
						.title(title)
						.content(content)
						.writeDate(writeDate)
						.cList(cList)
						.orNo(orNo).grOr(grOr).grDepth(grDepth).build();
		return boardDto;
	}

	
	@Builder
	public Board(Integer no, String author, String password, String title, String content, LocalDateTime writeDate,
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

}
