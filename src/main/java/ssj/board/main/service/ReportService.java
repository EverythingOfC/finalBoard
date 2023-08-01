package ssj.board.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ssj.board.main.dto.ReportDto;
import ssj.board.main.entity.Comment;
import ssj.board.main.entity.Report;
import ssj.board.main.repository.CommentRepository;
import ssj.board.main.repository.ReportRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;

    public void reportRegister(ReportDto reportDto,Integer coNo){
        Optional<Comment> comment = this.commentRepository.findById(coNo);  // 댓글 조회
        if(comment.isPresent()){
            reportDto.setComment(comment.get());
            this.reportRepository.save(reportDto.toEntity());
        }
    }

    public void reportDelete(Integer id){
        Optional<Report> report = this.reportRepository.findById(id);
        if(report.isPresent())
            this.reportRepository.delete(report.get());
    }

    public ReportDto reportView(Integer id){
        Optional<Report> reportView = this.reportRepository.findById(id);
        return reportView.isPresent()? reportView.get().toDto() : null;
    }
    public Page<ReportDto> reportList(int cPage,String or, Integer coNo){
        Pageable pageable = null;

        if(or.equals("desc"))
            pageable = PageRequest.of(cPage, 5, Sort.by("writeDate").descending());	// 최신 순
        else
            pageable = PageRequest.of(cPage, 5, Sort.by("writeDate").ascending());	// 오래된 순

        return this.reportRepository.findAllByComment_CoNo(pageable,coNo).map(Report::toDto);
    }

    // 댓글의 신고 수
    public int reportCommentCount(Integer commentId){	// 댓글 신고 횟수
        return this.reportRepository.reportCommentCount(commentId);
    }

}
