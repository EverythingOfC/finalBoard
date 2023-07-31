package ssj.board.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssj.board.main.entity.Report;


public interface ReportRepository extends JpaRepository<Report, Integer> {

    @Query("select count(R) from Report R where R.comment.coNo = :commentId")	// 댓글 신고 횟수
    Integer reportCommentCount(@Param(value = "commentId")Integer commentId);

}
