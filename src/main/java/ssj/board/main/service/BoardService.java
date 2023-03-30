package ssj.board.main.service;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ssj.board.main.dto.BoardDto;
import ssj.board.main.entity.Board;
import ssj.board.main.repository.BoardRepository;

@Service
@RequiredArgsConstructor
public class BoardService {

	private final BoardRepository boardRepository;

	public boolean pwdCheck(HttpServletResponse response, String password, Integer no) { // 비밀번호가 일치하면 해당 게시글을 수정하거나 삭제
																							// 가능
		Optional<Board> board = this.boardRepository.findByPasswordAndNo(password, no);
		if (board.isPresent())
			return true;
		else
			return false;
		
	}

	public String changeS(String result, Integer no, int page) { // 클릭한 값에 따라 수정 or 삭제를 행함.
		return result.equals("update") ? ("redirect:/board/updateForm?no=" + no + "&listPage=" + page)
				: "redirect:/board/delete?no=" + no + "&listPage=" + page;
	}

	public void create(BoardDto boardDto) { // 등록
		this.boardRepository.save(boardDto.toEntity());
	}

	public void update(BoardDto boardDto) { // 수정
		this.boardRepository.save(boardDto.toEntity());
	}

	public void delete(Integer no) { // 삭제
		Optional<Board> board = this.boardRepository.findByNo(no);
		if (board.isPresent())
			this.boardRepository.delete(board.get());
	}

	public Page<BoardDto> boardList(int page, String or, String or2, int view, String detail, String search) { // 전체 조회
																												// (
		Pageable pageable = null;
		if (or2.equals("desc")) 	// 정렬 조건
			pageable = PageRequest.of(page, view, Sort.by(or).descending()); // 내림차순
		else
			pageable = PageRequest.of(page, view, Sort.by(or).ascending()); // 오름차순

		if (detail.equals("title")) // 검색 조건
			return this.boardRepository.findAllByTitleContaining(pageable, search).map(Board::toDto); // 제목 검색
		else
			return this.boardRepository.findAllByAuthorContaining(pageable, search).map(Board::toDto); // 작성자 검색

	}

	public BoardDto boardView(Integer no) {
		Optional<Board> board = this.boardRepository.findByNo(no);
		return board.isPresent() ? board.get().toDto() : null;
	}

	public boolean badWordFilter(String word) {
		BadWord badWord = new BadWordImpl();

		return badWord.test(word) ? true : false; // 정상 단어이면 true, 비속어 or 선정적인 언어면 false
	}

	
	// 페이징 처리 방식(직접 구현)
	
	public int getTotalPage(long totalElements,int displayRow) {
		int totalPage = (int)(totalElements/displayRow);
	
		return (totalElements % displayRow) == 0 ? totalPage : totalPage+ 1;	// 나머지가 있으면 1증가  
	}
	
	public int getStartPage(int displayPage,int currentPage) {
		return ((currentPage - 1) / displayPage) * displayPage + 1;
	}
	
	public int getEndPage(int startPage,int totalPage,int displayPage) {
		int endPage = startPage + displayPage - 1;

		return endPage > totalPage ? totalPage : endPage; 
	}
	
	public boolean prev(int startPage) {
		return startPage==1? false : true;
	}
	
	public boolean next(int endPage,int totalPage) {
		return endPage == totalPage? false : true;
	}
}
