package ssj.board.main.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ssj.board.main.entity.Board;
import ssj.board.main.entity.Comment;
import ssj.board.main.entity.FilePack;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
	
	private List<FilePack> filePacks;	// 파일 정보

	private Integer parentNo;	// 부모 글의 일련번호
	
	private Integer childNo;	// 현재 글의 일련번호
	
	private Integer parentOr;	// 부모 글에서 파생된 답글의 순서
	
	private Boolean removeC;	// 삭제 여부
	
	private String relation;	// 부모글의 관계 - 부모글에서 파생된 답글의 수 
	
	public Board toEntity() {
		Board board = Board.builder().no(no).author(author).password(password).title(title).cList(cList)
				.content(content).writeDate(writeDate).orNo(orNo).grOr(grOr).grDepth(grDepth)
				.filePacks(filePacks)
				.parentNo(parentNo)
				.childNo(childNo)
				.parentOr(parentOr)
				.filePacks(filePacks)
				.removeC(removeC)
				.relation(relation)
				.build();
		return board;
	}

	
	public void boardUpdate(String author, String password, String title, String content, LocalDateTime writeDate) {
		this.author = author;
		this.password = password;
		this.title = title;
		this.content = content;
		this.writeDate = writeDate;	
	}
	
}
