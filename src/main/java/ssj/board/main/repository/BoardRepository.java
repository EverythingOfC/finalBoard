package ssj.board.main.repository;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ssj.board.main.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer>{
	
	Page<Board> findAllByTitleContaining(Pageable pageable,String title);		// 제목으로 조회, 인기순이나 최신순으로 오름차순
	Page<Board> findAllByAuthorContaining(Pageable pageable,String author);		// 작성자로 조회, 인기순이나 최신순으로 오름차순
	
	Optional<Board> findByNo(Integer no);	// 상세 조회
	Optional<Board> findByPasswordAndNo(String password,Integer no);// 비밀번호가 일치하면 해당 게시글 접근
	
}
