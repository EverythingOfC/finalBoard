package ssj.board.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ssj.board.main.entity.FilePack;

public interface FileRepository extends JpaRepository<FilePack, Integer>{
	
	@Query("select f from FilePack f where f.board.no = :no ")
	List<FilePack> findByBoardId(@Param(value="no")Integer no);	// 게시글의 파일을 모두 찾아옴
	
}
