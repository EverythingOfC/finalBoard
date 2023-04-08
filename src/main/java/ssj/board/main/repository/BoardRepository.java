package ssj.board.main.repository;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ssj.board.main.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer>{
	
	Page<Board> findAllByTitleContaining(Pageable pageable,String title);	// 제목으로 조회, 인기순이나 최신순으로 오름차순
	Page<Board> findAllByAuthorContaining(Pageable pageable,String author);	// 작성자로 조회, 인기순이나 최신순으로 오름차순
	
	Optional<Board> findByNo(Integer no);			// 상세 조회
	Optional<Board> findByPasswordAndNo(String password,Integer no);// 비밀번호가 일치하면 해당 게시글 접근
	
	@Transactional 
	@Modifying 
	@Query("update Board b set b.grOr = b.grOr + 1 "
			+ "where b.no = :no or orNo = :orNo and grOr > :grOr")  // 현재 답글에 답글을 추가하면, 추가한 답글과 현재 답글의 순서보다 높은 답글의 순서를 모두 1씩 증가시킴.
	void updateGr(@Param("no")Integer no, @Param("orNo")Integer orNo,@Param("grOr")Integer grOr);	// 그룹내의 순서 증가
	
	@Transactional	
	@Modifying 
	@Query("delete from Board b where b.no = :no or b.orNo = :orNo and b.grDepth > :depth")  // 선택한 글과 해당 그룹의 하위 답변들을 모두 삭제
	void deleteAllByCondition(@Param("no")Integer no,@Param("orNo")Integer orNo,@Param("depth")Integer depth);
	
	@Query("select b from Board b order by b.cList.size()")
	void selectAll();
	
	
}
