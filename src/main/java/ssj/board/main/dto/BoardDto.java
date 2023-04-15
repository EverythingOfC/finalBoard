package ssj.board.main.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ssj.board.main.entity.Board;
import ssj.board.main.entity.Comment;
import ssj.board.main.entity.FilePack;

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
	
	private List<FilePack> filePacks;	// 파일 정보

	private Integer parentNo;	// 부모 답글의 일련번호
	
	private Integer childNo;	// 자신의 일련번호
	
	private Boolean removeC;	// 삭제 여부
	
	public Board toEntity() {
		Board board = Board.builder().no(no).author(author).password(password).title(title).cList(cList)
				.content(content).writeDate(writeDate).orNo(orNo).grOr(grOr).grDepth(grDepth)
				.filePacks(filePacks)
				.parentNo(parentNo)
				.childNo(childNo)
				.filePacks(filePacks)
				.removeC(removeC)
				.build();
		return board;
	}

	@Builder
	public BoardDto(Integer no, String author, String password, String title, String content, LocalDateTime writeDate,
			List<Comment> cList, Integer orNo, Integer grOr, Integer grDepth, List<FilePack> filePacks,
			Integer parentNo, Integer childNo, Boolean removeC) {
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
		this.filePacks= filePacks;
		this.parentNo = parentNo;
		this.childNo = childNo;
		this.removeC= removeC;
	}
	
	public void boardUpdate(String author, String password, String title, String content, LocalDateTime writeDate) {
		this.author = author;
		this.password = password;
		this.title = title;
		this.content = content;
		this.writeDate = writeDate;	
	}
	
	/*
	 * public String getBoardOrNo() {
	 * 
	 * String orderNo = "";
	 * 
	 * if(this.grDepth > 1) { // 답변일 경우 StringBuilder sb = new StringBuilder();
	 * for(int i = 1; i < this.grDepth; i++) { sb.append("&nbsp;&nbsp;"); }
	 * sb.append(this.orNo); orderNo = sb.toString(); } else { // 답변이 아닐 경우 orderNo
	 * = "-"; } return orderNo; }
	 */
}
