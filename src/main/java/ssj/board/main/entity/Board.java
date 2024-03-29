package ssj.board.main.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import ssj.board.main.dto.BoardDto;

@Getter
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@DynamicInsert  // 구문 생성 시, null이 아닌 칼럼만 포함함.
@DynamicUpdate
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
	

	@CreationTimestamp	// insert쿼리 발생 시, 현재 시간 값 적용
	@Column(columnDefinition = "DATETIME")  // 8바이트(날짜와 시간을 같이 나타냄)
	private LocalDateTime writeDate;	// 작성일자

	private Integer orNo;	// 원글의 그룹 번호

	@ColumnDefault("0")
	private Integer grOr;	// (원글과 답글)그룹 순서

	@ColumnDefault("0")
	private Integer grDepth;// 답글의 깊이
	
	@OneToMany(mappedBy = "board", fetch = FetchType.LAZY,orphanRemoval = true, cascade = CascadeType.ALL)	// 연관관계의 주인을 외래키 필드로 함
	private List<Comment> cList;		// 댓글 목록
	
	@Builder.Default	// orphanRemoval: 부모와 자식간의 연관관계가 끊어졌을 경우, 자식 엔티티를 모두 제거
						// 부모 엔티티에서 자식 엔티티를 제거했을 때, 연결된 자식 엔티티를 제거해줌.
						// Cascade만 쓰면, 논리적으로 참조를 변경시킬 뿐, 자식 엔티티에서 데이터는 그대로 남게된다.
	@OneToMany(mappedBy = "board",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FilePack> filePacks = new ArrayList<FilePack>(); 	
	
	private Integer parentNo;	// 부모 글의 일련번호

	@ColumnDefault("0")
	private Integer parentOr;	// 부모 글에서 파생된 답글의 순서

	private Boolean removeC;	// 삭제 여부
	
	@Column(length=1000)
	private String relation;	// 부모글의 관계 - 부모글에서 파생된 답글의 수 

	@ColumnDefault("0")
	private Integer views;		// 조회 수

	@ColumnDefault("0")
	private Integer recommand;	// 추천 수

	public BoardDto toDto() {
		BoardDto boardDto = BoardDto.builder().no(no)
						.author(author)
						.password(password)
						.title(title)
						.content(content)
						.writeDate(writeDate)
						.cList(cList)
						.orNo(orNo).grOr(grOr).grDepth(grDepth)
						.parentNo(parentNo)
						.parentOr(parentOr)
						.filePacks(filePacks)
						.removeC(removeC)
						.relation(relation)
				.views(views)
				.recommand(recommand)
						.build();
		return boardDto;
	}

}
