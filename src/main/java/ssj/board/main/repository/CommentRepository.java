package ssj.board.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ssj.board.main.entity.Comment;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer>{
	Page<Comment> findAllByBoard_No(Pageable pageable,Integer board_No);	// 댓글 페이징
}
