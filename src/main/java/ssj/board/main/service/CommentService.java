package ssj.board.main.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ssj.board.main.dto.BoardDto;
import ssj.board.main.dto.CommentDto;
import ssj.board.main.entity.Comment;
import ssj.board.main.repository.BoardRepository;
import ssj.board.main.repository.CommentRepository;
import ssj.board.main.repository.ReportRepository;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;


	public void create(CommentDto commentDto,BoardDto boardDto) {

		Comment comment = CommentDto.builder().author(commentDto.getAuthor()).password(commentDto.getPassword()).
				content(commentDto.getContent())
				.writeDate(LocalDateTime.now()).board(boardDto.toEntity()).build().toEntity();

		this.commentRepository.save(comment);
	}

	public void update(CommentDto commentDto,BoardDto boardDto) {
		CommentDto cDto = commentView(commentDto.getCoNo());
		cDto.commentUpdate(commentDto.getAuthor(), commentDto.getPassword(), commentDto.getContent(), LocalDateTime.now(),boardDto.toEntity());

		this.commentRepository.save(commentDto.toEntity());
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
		
		return this.commentRepository.findAllByBoard_No(pageable,no).map(Comment::toDto);
	}
	

	public CommentDto commentView(Integer no) {
		Optional<Comment> comment = this.commentRepository.findById(no);
		return comment.isPresent() ? comment.get().toDto() : null;
	}

}
