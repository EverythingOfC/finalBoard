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

	Optional<Comment> findByPasswordAndCoNo(String password,Integer cNo);	// 비밀번호가 일치하면 해당 게시글 접근
	Page<Comment> findAllByBoard_no(Pageable pageable,Integer board_no);	// 댓글 페이징
}
