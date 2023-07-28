package ssj.board.main.controller;

import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import ssj.board.main.dto.BoardDto;
import ssj.board.main.dto.CommentDto;
import ssj.board.main.entity.FilePack;
import ssj.board.main.service.BoardService;
import ssj.board.main.service.CommentService;
import ssj.board.main.service.FileService;

@Controller
@RequiredArgsConstructor
public class BoardController {

	private final BoardService boardService;
	private final CommentService commentService;
	private final FileService fileService;

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

		if (!this.boardService.badWordFilter(search)) { // 욕설, 선정적 단어이면
			search = "";
			model.addAttribute("validate",false);
		}
		if(page < 1) page = 1;
		if(view < 10) view = 10;
		else if(view>20) view = 20;

		search = search.trim();	// 공백 제거

		Page<BoardDto> boardList = this.boardService.boardList(page - 1, or , view, detail, search);
		Integer dCount = this.boardService.removeCount(detail, search);

		model.addAttribute("dCount", dCount);	 // 삭제 된 게시글 수
		model.addAttribute("list", boardList);	 // 전체 요소
		model.addAttribute("listPage", page); 	 // 페이지 번호
		model.addAttribute("detail", detail); 	 // 검색 항목
		model.addAttribute("search", search);	 // 검색어
		model.addAttribute("view",view);	  	 // 검색조건
		model.addAttribute("or",or);		   	 // 인기, 최신, 제목, 작성자

		return "list";
	}

	@GetMapping("/board/view")
	public String view(Model model, int listPage, Integer no,
					   @RequestParam(value="or",defaultValue="desc")String or,
					   @RequestParam(value="cPage",defaultValue="1")int cPage
	) {

		BoardDto boardDto = this.boardService.boardView(no);

		Page<CommentDto> commentList = this.commentService.commentList(cPage-1, or,no);

		List<FilePack> filePacks = boardDto.getFilePacks();
		long total = 0;
		for(FilePack f : filePacks) {
			total += f.getSize();
		}

		model.addAttribute("list", commentList);
		model.addAttribute("boardView", boardDto);
		model.addAttribute("total",total);
		model.addAttribute("listPage", listPage);
		model.addAttribute("cPage", cPage);
		model.addAttribute("no", no);
		model.addAttribute("or",or);

		return "detail";
	}

	@GetMapping("/board/recommend")
	public String recommend(Model model,@RequestParam(value="no")int no,
	@RequestParam(value = "listPage",defaultValue="1") int page){

		this.boardService.increaseRecommend(no);	// 추천 수 증가

		return "redirect:/board/view?no="+no +"&listPage="+page;
	}


	@GetMapping("/board/createForm") // 새 글 및 답글 등록 폼
	public String createForm(Model model,@RequestParam(value="no",defaultValue = "0") int no	// 새 글이면 0, 답글이면 원글의 고유 번호
			,@RequestParam(value = "listPage",defaultValue="1") int page) {

		model.addAttribute("no",no);
		model.addAttribute("listPage",page);

		return "create";
	}


	@PostMapping("/board/create") // 원글 or 답글 등록
	public String create(@ModelAttribute BoardDto board,
						 @ModelAttribute MultipartFile[] files)   // 넘어가는 파일들
			throws Exception {


		BoardDto check = this.boardService.boardView(board.getNo());	// 없으면 null, 있으면 원글

		BoardDto boardDto = BoardDto.builder().author(board.getAuthor()).password(board.getPassword()).title(board.getTitle()).content(board.getContent())
				.writeDate(LocalDateTime.now()).orNo(0).grOr(0).grDepth(0).parentNo(0).parentOr(0).recommand(0).build();		// 얻어온 값으로 초기화

		BoardDto result = this.boardService.create(boardDto);	// 게시글 내용을 먼저 저장

		this.fileService.upload(files,result);	// 파일업로드 처리

		this.boardService.orGrCheck(result,check);	// 원글인지 답글인지 판별 후 저장

		return "redirect:/board/list";
	}

	@GetMapping("/board/updateForm") // 수정
	public String update(Model model, @RequestParam(value = "no") Integer no,
						 @RequestParam(value = "listPage") int page
	){

		BoardDto boardView = this.boardService.boardView(no);
		Integer countSize = this.fileService.countSize(no);	// 해당 게시글의 파일 전체 크기

		if(countSize==null)
			model.addAttribute("countSize",0);
		else
			model.addAttribute("countSize",countSize);


		model.addAttribute("boardView", boardView);
		model.addAttribute("listPage", page);

		return "update";	 // 수정 후 목록
	}

	@PostMapping("/board/update") // 수정
	public String update(
			@RequestParam(value = "listPage") int page,
			@ModelAttribute BoardDto board,
			@ModelAttribute MultipartFile[] files,
			@RequestParam(value = "dFile") String dFile	// 삭제 클릭된 파일
	)throws Exception{

		if(dFile.trim().length()>0) {	// 삭제버튼이 클릭된 파일이 있으면
			String[] dFiles = dFile.trim().split(" ");	// fId들을 불러옴.

			for(String d : dFiles) {
				this.fileService.deleteFile(Integer.parseInt(d));	// 해당 파일들을 삭제
			}
		}

		BoardDto boardView = this.boardService.boardView(board.getNo());	    // 파일이 삭제된 후, 해당 게시글을 다시 조회한다.
		boardView.boardUpdate(board.getAuthor(), board.getPassword(), board.getTitle(), board.getContent(), LocalDateTime.now());	// 값을 업데이트 후

		BoardDto update = this.boardService.create(boardView);	// 저장

		this.fileService.upload(files,update);	// 파일 수정 처리


		return "redirect:/board/list?listPage="+page;	 // 수정 후 목록
	}

	@GetMapping("/board/delete") // 게시글 삭제
	public String delete(@RequestParam(value = "no") Integer no) throws Exception{

		this.boardService.deleteBoard(no);

		return "redirect:/board/list";
	}


	@GetMapping("/download")
	public void fileDownload(@RequestParam(value="savedPath")String savedPath,HttpServletResponse response) throws Exception{

		this.fileService.download(savedPath,response);

	}


	@GetMapping("/board/downloadExcel")
	public void saveCsv(String order,String detail,String search,HttpServletResponse response)throws Exception{

		this.boardService.saveCsv(response,order,detail,search);
	}


}
