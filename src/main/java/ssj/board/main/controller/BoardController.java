package ssj.board.main.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import ssj.board.main.dto.BoardDto;
import ssj.board.main.dto.CommentDto;
import ssj.board.main.service.BoardService;
import ssj.board.main.service.CommentService;

@Controller
@RequiredArgsConstructor
public class BoardController {

	private final BoardService boardService;
	private final CommentService commentService;

	@GetMapping(value = { "/", "/board", "/index" })
	public String index() {
		return "redirect:/board/list";
	}

	@GetMapping("/board/list") // 전체 조회
	public String board(Model model,
			@RequestParam(value = "or",defaultValue="desc")String or,	// 원글 최신 or 오래된 정렬
			@RequestParam(value = "view",defaultValue="10") int view,	// 한 페이지에 조회될 게시글 수
			@RequestParam(value = "listPage", defaultValue = "1") int page,	// 현재 페이지
			@RequestParam(value = "detail", defaultValue = "title") String detail,	// 검색 유형
			@RequestParam(value = "search", defaultValue = "") String search,	// 검색 글자
			@RequestParam(value = "result",defaultValue="")String result	// 수정, 삭제 후의 alert 내용 값
			){	

		if (!this.boardService.badWordFilter(search)) { // 정상적인 단어가 아니면
			return "redirect:/board/list?listPage="+page;
		}
	
		if(page < 1)
			page = 1;
		if(view < 10)
			view = 10;
		
		Page<BoardDto> boardList = this.boardService.boardList(page - 1, or , view, detail, search);
		
		model.addAttribute("list", boardList);	 // 전체 요소
		model.addAttribute("listPage", page); 	 // 페이지 번호
		model.addAttribute("detail", detail); 	 // 검색 항목
		model.addAttribute("search", search);	 // 검색어
		model.addAttribute("view",view);	  	 // 검색조건
		model.addAttribute("or",or);		   	 // 인기, 최신, 제목, 작성자
		model.addAttribute("result",result);	 // 수정, 삭제후의 결과값 저장(기본값은 "")
		
		return "list";
	}

	@GetMapping("/board/view")
	public String view(Model model, int listPage, Integer no,
			@RequestParam(value="or",defaultValue="desc")String or,
			@RequestParam(value="cPage",defaultValue="1")int cPage,
			@RequestParam(value="result",defaultValue="")String result
			) {

		BoardDto boardDto = this.boardService.boardView(no);
		
			
		Page<CommentDto> commentList = this.commentService.commentList(cPage-1, or,no);
		
		model.addAttribute("list", commentList);
		model.addAttribute("boardView", boardDto);
		model.addAttribute("listPage", listPage);
		model.addAttribute("cPage", cPage);
		model.addAttribute("no", no);
		model.addAttribute("or",or);
		model.addAttribute("result",result);
		
		return "detail";
	}

	
	
	
	@GetMapping("/board/createForm") // 새 글 및 답글 등록 폼
	public String createForm(Model model,@RequestParam(value="no",defaultValue = "0") int no	// 새 글이면 0, 답글이면 원글의 고유 번호
			,@RequestParam(value = "listPage",defaultValue="1") int page) {
		
		model.addAttribute("no",no);
		model.addAttribute("listPage",page);

		return "create";
	}
	
	
	@PostMapping("/board/create") // 원글 or 답글 등록
	public String create(@RequestParam(value = "author") String author,
			@RequestParam(value = "password") String password, @RequestParam(value = "title") String title,
			@RequestParam(value = "content") String content,
			@RequestParam(value = "no",defaultValue = "0") int no,	// 원글인지 답글인지 판별 
			@RequestParam(value = "listPage") int page) {	
		
		BoardDto check = this.boardService.boardView(no);	// 없으면 null, 있으면 원글
		
		BoardDto boardDto = BoardDto.builder().author(author).password(password).title(title).content(content)
				.writeDate(LocalDateTime.now()).orNo(0).grOr(1).grDepth(1).build();		// 얻어온 값으로 초기화
		
		BoardDto result = this.boardService.create(boardDto);	// 게시글 내용을 먼저 저장
		
		if(check == null) {	// 새글
			result.setOrNo(result.getNo());	// 게시글 고유 번호로 그룹 번호를 초기화
			this.boardService.create(result);		// 원글 등록
		}else {				// 답글
			result.setOrNo(check.getOrNo());		// 원글의 그룹 번호로 초기화
			result.setGrOr(check.getGrOr());		// 원글의 그룹 순서로 초기화
			result.setGrDepth(check.getGrDepth()+1);// 깊이 증가
			this.boardService.create(result);		// 답글 등록
			this.boardService.updateGr(result.getNo(), result.getOrNo(), result.getGrOr());
		}
		
		return "redirect:/board/list";
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
			@RequestParam(value = "listPage") int page) {

		boolean change = this.boardService.pwdCheck(password, no);
		
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
	public String update(@RequestParam(value = "no") Integer no,@RequestParam(value = "password") String password, 
			@RequestParam(value = "title") String title,@RequestParam(value = "content") String content,
			@RequestParam(value = "author") String author,@RequestParam(value = "listPage") int page
			){

		BoardDto boardView = this.boardService.boardView(no);
		boardView.boardUpdate(author, password, title, content, LocalDateTime.now());

		this.boardService.update(boardView);

		return "redirect:/board/list";	 // 수정 후 목록
	}

	@GetMapping("/board/delete") // 삭제
	public String delete(@RequestParam(value = "no") Integer no) {

		BoardDto boardView = this.boardService.boardView(no);
		
		if(boardView != null)
			this.boardService.deleteAllByGrDepthGreaterThan(no,boardView.getOrNo(),boardView.getGrDepth());	// 해당 글과 해당 글의 답변들을 모두 삭제
		
		return "redirect:/board/list?result=delete";
	}
}
