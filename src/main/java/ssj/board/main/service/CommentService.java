package ssj.board.main.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ssj.board.main.dto.CommentDto;
import ssj.board.main.entity.Comment;
import ssj.board.main.repository.CommentRepository;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;

	public void create(CommentDto commentDto) {
		this.commentRepository.save(commentDto.toEntity()).toDto();
	}

	public void update(CommentDto commentDto) {
		this.commentRepository.save(commentDto.toEntity()).toDto();
	}

	public void delete(Integer no) {
		Optional<Comment> comment = this.commentRepository.findById(no);
		if (comment.isPresent())
			this.commentRepository.delete(comment.get());
	}

	public Page<CommentDto> commentList(int cPage,String or, int no) {
		
		Pageable pageable = null;
		
		if(or.equals("desc"))
			pageable = PageRequest.of(cPage, 5, Sort.by("writeDate").descending());	// 최신 순
		else
			pageable = PageRequest.of(cPage, 5, Sort.by("writeDate").ascending());	// 오래된 순
		
		return this.commentRepository.findAllByBoard_no(pageable,no).map(Comment::toDto);
	}
	

	public CommentDto commentView(Integer no) {
		Optional<Comment> comment = this.commentRepository.findById(no);
		return comment.isPresent() ? comment.get().toDto() : null;
	}

	public boolean pwdCheck(String password, Integer coNo) { // 비밀번호가 일치하면 해당 게시글을 수정하거나 삭제
		Optional<Comment> comment = this.commentRepository.findByPasswordAndCoNo(password, coNo);
		return comment.isPresent()? true : false;

	}

	public String changeS(String result, Integer no, Integer coNo, int page) { // 클릭한 값에 따라 수정 or 삭제를 행함.
		return result.equals("update") ? ("redirect:/board/cUpdateForm?coNo=" + coNo + "&listPage=" + page +"&no="+no)
				: "redirect:/board/cDelete?coNo=" + coNo +"&no="+no +"&listPage="+page;
	}
}
