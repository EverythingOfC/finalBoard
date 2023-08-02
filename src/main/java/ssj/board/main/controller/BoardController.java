package ssj.board.main.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping(value = {"/", "/board", "/index"})
    public String index() {
        return "redirect:/board/list";
    }

    @GetMapping("/board/list") // 전체 조회
    public String board(Model model,
                        @RequestParam(value = "or", defaultValue = "desc") String or,    // 원글 최신 or 오래된 정렬
                        @RequestParam(value = "view", defaultValue = "10") int view,    // 한 페이지에 조회될 게시글 수
                        @RequestParam(value = "listPage", defaultValue = "1") int page,    // 현재 페이지
                        @RequestParam(value = "detail", defaultValue = "title") String detail,    // 검색 유형
                        @RequestParam(value = "search", defaultValue = "") String search,    // 검색 글자
                        @RequestParam(value = "result", defaultValue = "") String result    // 수정, 삭제 후의 alert 내용 값
    ) {

        if (page < 1) page = 1;
        if (view < 10) view = 10;
        else if (view > 20) view = 20;

        Page<BoardDto> boardList = this.boardService.boardList(page - 1, or, view, detail, search);
        Integer dCount = this.boardService.removeCount(detail, search);

        model.addAttribute("dCount", dCount);     // 삭제 된 게시글 수
        model.addAttribute("list", boardList);    // 전체 요소
        model.addAttribute("listPage", page);     // 페이지 번호
        model.addAttribute("detail", detail);     // 검색 항목
        model.addAttribute("search", search);     // 검색어
        model.addAttribute("view", view);         // 검색조건
        model.addAttribute("or", or);             // 인기, 최신, 제목, 작성자

        return "list";
    }

    @GetMapping("/board/view")
    public String view(Model model, Integer no, int listPage,  HttpServletRequest request,
                       @RequestParam(value = "or", defaultValue = "desc") String or,
                       @RequestParam(value = "time", required = false) Long time,
                       @RequestParam(value = "cPage", defaultValue = "1") int cPage
    ) {
        HttpSession session = request.getSession();    // 서버에서 클라이언트 식별을 위해 고유한 세션 ID를 쿠키로 저장
        long currentTime = new Date().getTime();

        if (this.boardService.increaseViews(no, time, currentTime)) {    // 조회 수가 증가할 차례이면
            session.setAttribute("lastViews" + no, currentTime);    // 마지막으로 조회한 시간을 저장
            return "redirect:/board/view?no=" + no + "&listPage=" + listPage + "&time=" + currentTime + "&cPage="+ cPage;
        }

        BoardDto boardDto = this.boardService.boardView(no);
        Page<CommentDto> commentList = this.commentService.commentList(cPage - 1, or, no);
        Integer total = this.fileService.countSize(no);    // 파일 크기 합

        if(total!=null)
            model.addAttribute("total", String.format("%.2f",total/(1024.0) ));
        model.addAttribute("list", commentList);
        model.addAttribute("boardView", boardDto);
        model.addAttribute("listPage", listPage);
        model.addAttribute("cPage", cPage);
        model.addAttribute("no", no);
        model.addAttribute("or", or);

        return "detail";
    }

    @GetMapping("/board/recommend")
    public String recommend(@RequestParam(value = "no") int no, HttpServletRequest request,
                            @RequestParam(value = "timeR") Long timeR,
                            @RequestParam(value = "listPage", defaultValue = "1") int page) {

        HttpSession session = request.getSession();    // 서버에서는 클라이언트를 구분하기 위해 고유한 세션 ID를 부여함.
        this.boardService.increaseRecommend(no);

        session.setAttribute("lastRecommend" + no, timeR);    // 마지막으로 추천한 시간을 저장

        return "redirect:/board/view?no=" + no + "&listPage=" + page + "&time=" + session.getAttribute("lastViews" + no).toString();
    }

    @GetMapping("/board/createForm") // 새 글 및 답글 등록 폼
    public String createForm(Model model, @RequestParam(value = "no", defaultValue = "0") int no    // 새 글이면 0, 답글이면 원글의 고유 번호
            , @RequestParam(value = "listPage", defaultValue = "1") int page) {

        model.addAttribute("no", no);
        model.addAttribute("listPage", page);

        return "create";
    }


    @PostMapping("/board/create") // 원글 or 답글 등록
    public String create(@ModelAttribute BoardDto boardDto,
                         @ModelAttribute MultipartFile[] files) throws Exception {

        BoardDto check = this.boardService.boardView(boardDto.getNo());    // 없으면 null, 있으면 원글
        BoardDto writeContent = this.boardService.create(boardDto);    // 게시글 내용을 먼저 저장

        this.fileService.upload(files, writeContent);    // 파일업로드 처리
        this.boardService.orGrCheck(writeContent, check);    // 원글인지 답글인지 판별 후 저장

        return "redirect:/board/list";
    }

    @GetMapping("/board/updateForm") // 수정
    public String update(Model model, @RequestParam(value = "no") Integer no,
                         @RequestParam(value = "listPage") int page) {

        BoardDto boardView = this.boardService.boardView(no);

        if(this.fileService.viewFile(no)==null) // 게시글에 파일이 없으면
            model.addAttribute("countSize", 0);
        else
            model.addAttribute("countSize", this.fileService.countSize(no));

        model.addAttribute("boardView", boardView);
        model.addAttribute("listPage", page);

        return "update";     // 수정 후 목록
    }

    @PostMapping("/board/update") // 수정
    public String update(
            @RequestParam(value = "listPage") int page,
            @ModelAttribute BoardDto boardDto,
            @ModelAttribute MultipartFile[] files,
            @RequestParam(value = "dFile") String dFile    // 삭제 클릭된 파일
    ) {

        this.fileService.deleteFile(dFile);    // 해당 파일들을 삭제

        BoardDto boardView = this.boardService.boardView(boardDto.getNo());      // 파일이 삭제된 후, 해당 게시글을 다시 조회
        boardView.boardUpdate(boardDto.getAuthor(), boardDto.getPassword(), boardDto.getTitle(), boardDto.getContent(), LocalDateTime.now());    // 게시글 업데이트
        BoardDto update = this.boardService.update(boardView);    // 게시글 수정

        this.fileService.upload(files, update);    // 파일 수정 처리

        return "redirect:/board/list?listPage=" + page;     // 수정 후 목록
    }

    @GetMapping("/board/delete") // 게시글 삭제
    public String delete(@RequestParam(value = "no") Integer no) {

        this.boardService.deleteBoard(no);

        return "redirect:/board/list";
    }


    @GetMapping("/download")
    public void fileDownload(@RequestParam(value = "savedPath") String savedPath, HttpServletResponse response) throws IOException {
        this.fileService.download(savedPath, response);
    }


    @GetMapping("/board/downloadExcel")
    public void saveCsv(String order, String detail, String search, HttpServletResponse response) throws IOException {

        this.boardService.saveCsv(response, order, detail, search);

    }

}
