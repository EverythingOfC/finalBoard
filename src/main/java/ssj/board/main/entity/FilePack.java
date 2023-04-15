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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class FilePack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fId;	// 파일 고유 값

    private String originalName; // 원본 파일

    @Column(length = 500)
    private String savedName;	 // UUID로 바뀌어서 저장된 파일

    @Column(length = 500)
    private String savedPath;	 // 저장 경로

    private String type;		// 유형
    
    private long size;			// 파일 크기

    private LocalDateTime createdDate;	// 등록일
    
    @ManyToOne(fetch = FetchType.LAZY)	// 게시판
    @JoinColumn(name="board_id")
    private Board board;

	public FilePack(Integer fId, String originalName, String savedName, String savedPath, 
			String type,long size,LocalDateTime createdDate, Board board) {
		super();
		this.fId = fId;
		this.originalName = originalName;
		this.savedName = savedName;
		this.savedPath = savedPath;
		this.type = type;
		this.size = size;
		this.createdDate = createdDate;
		this.board = board;
	}
    
    
}