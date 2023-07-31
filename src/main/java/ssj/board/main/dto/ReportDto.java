package ssj.board.main.dto;

import lombok.*;
import ssj.board.main.entity.Comment;
import ssj.board.main.entity.Report;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ReportDto { // 신고 내역

    private Integer reNo;	// 신고 번호

    private String author;	// 작성자	(JPA에서 String의 길이는 default로 255이다.)

    private String password;// 비밀번호

    private String content;	// 내용

    private LocalDateTime writeDate;	// 작성일자

    private Comment comment;    // 신고내역이 있는 댓글

    public Report toEntity(){
        Report report = Report.builder().reNo(reNo)
                .author(author)
                .password(password)
                .content(content)
                .writeDate(writeDate)
                .comment(comment).build();
                return report;
    }
}
