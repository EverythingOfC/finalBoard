package ssj.board.main.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ssj.board.main.dto.BoardDto;

@Getter
@Setter
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
	
	@Column(columnDefinition = "MEDIUMINT")	// 부호 없으면 16777215까지 표시
	private Integer countS;	// 조회 수
	
	@Builder
	public Board(Integer no, String author, String password, String title, String content,
			LocalDateTime writeDate, Integer countS) {
		this.no = no;
		this.author = author;
		this.password = password;
		this.title = title;
		this.content = content;
		this.writeDate = writeDate;
		this.countS = countS;
	}
	
	public BoardDto toDto() {
		BoardDto boardDto = BoardDto.builder().no(no)
						.author(author)
						.password(password)
						.title(title)
						.content(content)
						.writeDate(writeDate)
						.countS(countS).build();
		return boardDto;
	}

}
