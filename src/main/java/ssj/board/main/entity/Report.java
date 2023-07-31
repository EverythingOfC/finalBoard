package ssj.board.main.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssj.board.main.dto.ReportDto;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@AllArgsConstructor
@Builder
@Getter
public class Report { // 신고 내역

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)	// 칼럼만의 독립적인 시퀀스
    private Integer reNo;	// 신고 번호

    @Column(nullable=false)
    private String author;	// 작성자	(JPA에서 String의 길이는 default로 255이다.)

    @Column(nullable=false)
    private String password;// 비밀번호

    @Column(nullable=false,length = 751)	// 내용은 751
    private String content;	// 내용

    @Column(columnDefinition = "DATETIME")  // 8바이트(날짜와 시간을 같이 나타냄)
    private LocalDateTime writeDate;	// 작성일자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="comment_id")   // 데이터 조회 시, 다른 테이블의 데이터를 같이 조회하기 위해서
    private Comment comment;

    public ReportDto toDto(){
        ReportDto report = ReportDto.builder().reNo(reNo)
                .author(author)
                .password(password)
                .content(content)
                .writeDate(writeDate)
                .comment(comment).build();
        return report;
    }
}
