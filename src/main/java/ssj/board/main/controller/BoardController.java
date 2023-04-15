package ssj.board.main.controller;

import java.io.File;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import ssj.board.main.dto.BoardDto;
import ssj.board.main.dto.CommentDto;
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

		if (!this.boardService.badWordFilter(search)) { // 정상적인 단어가 아니면
			return "redirect:/board/list?listPage="+page;
		}
	
		if(page < 1)
			page = 1;
		if(view < 10)
			view = 10;
		
		Page<BoardDto> boardList = this.boardService.boardList(page - 1, or , view, detail, search);
		int dCount = this.boardService.removeCount();
		
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
		
		model.addAttribute("list", commentList);
		model.addAttribute("boardView", boardDto);
		model.addAttribute("listPage", listPage);
		model.addAttribute("cPage", cPage);
		model.addAttribute("no", no);
		model.addAttribute("or",or);
		
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
	public String create(@RequestParam(value = "author") String author,	// 작성자
			@RequestParam(value = "password") String password, 	// 비밀번호
			@RequestParam(value = "title") String title,	// 제목
			@RequestParam(value = "content") String content,	// 내용
			@RequestParam(value = "no",defaultValue = "0") int no,	// 원글인지 답글인지 판별 
			@RequestParam(value = "listPage") int page,	// 페이지
			@RequestParam(value = "files")MultipartFile[] files	// 넘어가는 파일들
			)throws Exception {	
		
		
		BoardDto check = this.boardService.boardView(no);	// 없으면 null, 있으면 원글
		
		BoardDto boardDto = BoardDto.builder().author(author).password(password).title(title).content(content)
				.writeDate(LocalDateTime.now()).orNo(0).grOr(1).grDepth(1).build();		// 얻어온 값으로 초기화
		
		BoardDto result = this.boardService.create(boardDto);	// 게시글 내용을 먼저 저장
		
		if(!files[0].isEmpty()) {	// 넘어온 파일이 있다면
			this.fileService.upload(files,result);	// 파일업로드 처리
		}
		
		this.boardService.orGrCheck(result,check);	// 원글인지 답글인지 판별 후 저장
		
		return "redirect:/board/list";
	}
	
	@GetMapping("/board/updateForm") // 수정
	public String update(Model model, @RequestParam(value = "no") Integer no, 
			@RequestParam(value = "listPage") int page
			){

		BoardDto boardView = this.boardService.boardView(no);

		model.addAttribute("boardView", boardView);
		model.addAttribute("listPage", page);
		
		return "update";	 // 수정 후 목록
	}
	
	@PostMapping("/board/update") // 수정
	public String update(@RequestParam(value = "no") Integer no,@RequestParam(value = "password") String password, 
			@RequestParam(value = "title") String title,@RequestParam(value = "content") String content,
			@RequestParam(value = "author") String author,@RequestParam(value = "listPage") int page,
			@RequestParam(value = "files") MultipartFile[] files)throws Exception{

		BoardDto boardView = this.boardService.boardView(no);
		boardView.boardUpdate(author, password, title, content, LocalDateTime.now());

		BoardDto update = this.boardService.create(boardView);
		
		if(files.length > 0) {	// 넘어온 파일이 있다면
			this.fileService.update(files,update);	// 파일업로드 처리
		}

		return "redirect:/board/list";	 // 수정 후 목록
	}

	@GetMapping("/board/delete") // 게시글 삭제
	public String delete(@RequestParam(value = "no") Integer no) throws Exception{

		this.boardService.deleteBoard(no);
		
		return "redirect:/board/list";
	}
	

	@GetMapping("/download")
	public void fileDownload(@RequestParam(value="fileName")String fileName,HttpServletResponse response) throws Exception{
					
		this.fileService.download(fileName,response);
		
	}
	
	@GetMapping("/deleteFile")	// 파일 삭제
	public String fileDelete(@RequestParam(value="fId")Integer fId,
			@RequestParam(value="savedPath")String savedPath,
			@RequestParam(value="no")Integer no, Model model
			) {
		
		File file = new File(savedPath);	// 업로드 경로 + 파일명
		if(file.exists())	// 파일 존재하면
			file.delete();	// 지정한 경로의 파일 삭제
		
		/*
		 * BoardDto boardView = this.boardService.boardView(no); List<FilePack>
		 * filePacks = boardView.getFilePacks();
		 */
		
		
		this.fileService.deleteFile(fId);	// 아이디로 파일 삭제
		
		return "redirect:/board/updateForm/?no="+no;	// 삭제 후 수정폼으로 다시 이동
	}
	
	@GetMapping("/downloadExcel")
	public void saveCsv(HttpServletResponse response)throws Exception{
		
		this.boardService.saveCsv(response);
	}
	
	
}
