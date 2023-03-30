package ssj.board.main.controller;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import ssj.board.main.dto.BoardDto;
import ssj.board.main.service.BoardService;

@Controller
@RequiredArgsConstructor
public class BoardController {

	private final BoardService boardService;

	@GetMapping(value = { "/", "/board", "/index" })
	public String index() {
		return "redirect:/board/list";
	}

	@GetMapping("/board/list") // 전체 조회
	public String board(Model model,
			@RequestParam(value= "or",defaultValue="countS")String or,// 인기 순, 최신 순
			@RequestParam(value="or2",defaultValue="asc") String or2,	// 오름차순 내림차순
			@RequestParam(value = "view",defaultValue="5") int view,	// 한 페이지에 조회될 게시글 수
			@RequestParam(value = "listPage", defaultValue = "1") int page,
			@RequestParam(value = "detail", defaultValue = "title") String detail,
			@RequestParam(value = "search", defaultValue = "") String search, HttpServletResponse response,
			@RequestParam(value = "pro",defaultValue="false") boolean pro) {

		if (!this.boardService.badWordFilter(search)) { // 정상적인 단어가 아니면
			model.addAttribute("pro",true);	// 금지어 속성을 저장
			search = "";
		}
		
		if(or.equals("countS") || or.equals("writeDate"))	// 조회수와 최신순일때는 내림차순
			or2 = "desc";
		
		if(page < 1)
			page = 1;
		
		Page<BoardDto> boardList = this.boardService.boardList(page - 1, or ,or2, view, detail, search);

//		long totalCount = boardList.getTotalElements();
//		int totalPage= this.boardService.getTotalPage(totalCount, view);
//		int startPage = this.boardService.getStartPage(9,page);
//		int endPage = this.boardService.getEndPage(startPage, totalPage, 9);
//		boolean prev = this.boardService.prev(startPage);
//		boolean next = this.boardService.next(endPage, totalPage);
//		
//		PagingVo pagingVo = new PagingVo(totalCount,page,totalPage,view,9,startPage,endPage,prev,next);
		
//		model.addAttribute("paging", pagingVo);	 // 페이징
		
		model.addAttribute("list", boardList);	 // 전체 요소
		model.addAttribute("listPage", page); 	 // 페이지 번호
		model.addAttribute("detail", detail); 	 // 검색 항목
		model.addAttribute("search", search);	 // 검색어
		model.addAttribute("view",view);	  	 // 검색조건
		model.addAttribute("or",or);		   	 // 인기, 최신, 제목, 작성자
		model.addAttribute("or2",or2);		   	 // 오름차순, 내림차순

		return "list";
	}

	@GetMapping("/board/view")
	public String view(Model model, int listPage, Integer no) {

		BoardDto boardDto = this.boardService.boardView(no);
		
		boardDto.setCountS(boardDto.getCountS()+1);	// 조회 수 증가
		this.boardService.update(boardDto);
		
		model.addAttribute("boardView", boardDto);
		model.addAttribute("listPage", listPage);
		return "detail";
	}

	@GetMapping("/board/createForm") // 등록 폼
	public String createForm() {
		return "create";
	}

	@PostMapping("/board/create") // 등록
	public String create(@RequestParam(value = "author") String author,
			@RequestParam(value = "password") String password, @RequestParam(value = "title") String title,
			@RequestParam(value = "content") String content) {
		BoardDto boardDto = BoardDto.builder().author(author).password(password).title(title).content(content)
				.writeDate(LocalDateTime.now()).countS(0).build();

		if (!this.boardService.badWordFilter(author)) {
			return "create";
		}

		this.boardService.create(boardDto);

		return "redirect:/board/list?or=writeDate";	// 등록 후에는 최신순으로 변경함으로써 (본인이 작성한 글을 확인 가능)
	}

	@GetMapping("/board/pwdCheck") // 수정 or 삭제를 위한 비밀번호 입력 폼
	public String pwdCheck(Model model, Integer no, int listPage,String result,
			@RequestParam(value="correct",defaultValue="true")boolean correct) {	// 비밀번호 일치여부: correct

		model.addAttribute("no", no);
		model.addAttribute("listPage", listPage);
		model.addAttribute("result",result);
		model.addAttribute("correct", correct);

		return "pwdCheck";
	}

	@PostMapping("/board/change") // 비밀번호가 일치하면, 해당 게시글을 수정 or 삭제
	public String change(Model model, @RequestParam(value = "password") String password,
			@RequestParam(value = "no") Integer no, @RequestParam(value = "result") String result,
			@RequestParam(value = "listPage") int page, HttpServletResponse r) {

		boolean change = this.boardService.pwdCheck(r, password, no);
		
		return change ? this.boardService.changeS(result, no, page) : ("redirect:/board/pwdCheck?no="+no + "&listPage=" + page + "&correct=false&result="+result);
		// 일치하면 수정 or 삭제, 일치하지 않으면 목록으로
	}

	@GetMapping("/board/updateForm") // 수정 폼
	public String updateForm(Model model, @RequestParam(value = "no") Integer no,
			@RequestParam(value = "listPage") int page) {

		BoardDto boardDto = this.boardService.boardView(no);

		model.addAttribute("boardView", boardDto); // 속성을 고대로 가지고 감
		model.addAttribute("listPage", page); // 속성을 고대로 가지고 감

		return "update";
	}

	@PostMapping("/board/update") // 수정
	public String update(@RequestParam(value = "no") Integer no,@RequestParam(value = "password") String password, @RequestParam(value = "title") String title,
			@RequestParam(value = "content") String content, @RequestParam(value = "author") String author,
			@RequestParam(value = "countS")Integer countS){

		BoardDto boardDto = BoardDto.builder().no(no).author(author).password(password).title(title).content(content)
				.writeDate(LocalDateTime.now()).countS(countS).build();

		this.boardService.update(boardDto);

		return "redirect:/board/list?or=writeDate";	 // 수정 후 제일 최신 페이지로	
	}

	@GetMapping("/board/delete") // 삭제
	public String delete(@RequestParam(value = "no") Integer no, @RequestParam(value = "listPage") int page) {

		this.boardService.delete(no);

		return "redirect:/board/list?listPage=" + page;	// 삭제된 게시물이 있는 페이지 번호를 넘겨줌.
	}
}
