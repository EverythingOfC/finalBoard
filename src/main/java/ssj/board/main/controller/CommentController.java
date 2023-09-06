package ssj.board.main.controller;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import ssj.board.main.dto.BoardDto;
import ssj.board.main.dto.CommentDto;
import ssj.board.main.service.BoardService;
import ssj.board.main.service.CommentService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final BoardService boardService;


    @GetMapping("/board/commentCreateForm") // 등록 폼
    public String cCreateForm(Model model, @RequestParam(value = "no") int no,
                              @RequestParam(value = "listPage") int page) {

        model.addAttribute("no", no);
        model.addAttribute("listPage", page);
        return "cCreate"; // 댓글 이용
    }

    @PostMapping("/board/commentCreate") // 등록
    public String cCreate(@ModelAttribute CommentDto commentDto, @RequestParam(value = "no") Integer no, @RequestParam(value = "listPage") int page,
                          HttpServletRequest request) {

        HttpSession session = request.getSession();
        long currentTime = Long.parseLong(session.getAttribute("lastViews"+no).toString());
        BoardDto boardDto = this.boardService.boardView(no);
        this.commentService.create(commentDto, boardDto);

        return "redirect:/board/view?listPage=" + page + "&time=" +currentTime + "&no=" + no + "#comment0";
    }

    @GetMapping("/board/cUpdateForm") // 수정 폼
    public String updateForm(Model model, @RequestParam(value = "no") Integer no,
                             @RequestParam(value = "coNo") Integer coNo,
                             @RequestParam(value = "listPage") int page) {

        CommentDto commentDto = this.commentService.commentView(coNo);

        model.addAttribute("commentView", commentDto); // 댓글 view
        model.addAttribute("listPage", page); // 게시글 리스트 페이지
        model.addAttribute("no", no);    // 게시글 번호

        return "cUpdate";
    }

    @PostMapping("/board/cUpdate") // 수정
    public String update(@ModelAttribute CommentDto commentDto,
                         @RequestParam(value = "no") Integer no,
                         @RequestParam(value = "listPage") int page) {
        BoardDto boardView = this.boardService.boardView(no);
        this.commentService.update(commentDto, boardView);

        return "redirect:/board/view?listPage=" + page + "&no=" + no + "&or=desc#comment" + commentDto.getCoNo();
    }

    @GetMapping("/board/cDelete") // 삭제
    public String delete(@RequestParam(value = "no") Integer no,
                         @RequestParam(value = "listPage") int page,
                         @RequestParam(value = "coNo") Integer coNo) {

        this.commentService.delete(coNo);

        return "redirect:/board/view?listPage=" + page + "&no=" + no + "&or=desc#comment0";
    }

}
