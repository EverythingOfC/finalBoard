package ssj.board.main.controller;

import java.time.LocalDateTime;

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
	public String cCreate(@RequestParam(value = "author") String author,
			@RequestParam(value = "password") String password, @RequestParam(value = "content") String content,
			@RequestParam(value = "no", defaultValue = "0") int no, @RequestParam(value = "listPage") int page) {

		BoardDto boardDto = this.boardService.boardView(no);

		CommentDto commentDto = CommentDto.builder().author(author).password(password).title("").content(content)
				.writeDate(LocalDateTime.now()).board(boardDto.toEntity()).build();

		this.commentService.create(commentDto);

		return "redirect:/board/view?listPage=" + page + "&no=" + no + "#comment0";
	}

	@GetMapping("/board/cPwdCheck") // 수정 or 삭제를 위한 비밀번호 입력 폼 public String
	public String pwdCheck(Model model,Integer coNo, 
			Integer no, int listPage, String result,
			@RequestParam(value = "correct", defaultValue = "true") boolean correct
			) { // 비밀번호

		model.addAttribute("coNo", coNo);
		model.addAttribute("no", no);
		model.addAttribute("listPage", listPage);
		model.addAttribute("result", result);
		model.addAttribute("correct", correct);

		return "cPwdCheck";
	}
	
	@PostMapping("/board/cChange") // 비밀번호가 일치하면, 해당 게시글을 수정 or 삭제
	public String change(Model model, @RequestParam(value = "password") String password,
			@RequestParam(value = "coNo")Integer coNo,
			@RequestParam(value = "no") Integer no, @RequestParam(value = "result") String result,
			@RequestParam(value = "listPage") int page) {

		boolean change = this.commentService.pwdCheck(password, coNo);
		
		return change ? this.commentService.changeS(result, no,coNo, page) : ("redirect:/board/cPwdCheck?no="+no+"&coNo="+coNo + "&listPage=" + page + "&correct=false&result="+result);
		// 일치하면 수정 or 삭제, 일치하지 않으면 목록으로
	}
	@GetMapping("/board/cUpdateForm") // 수정 폼
	public String updateForm(Model model, @RequestParam(value = "no") Integer no,
			@RequestParam(value = "coNo")Integer coNo,
			@RequestParam(value = "listPage") int page) {

		CommentDto commentDto = this.commentService.commentView(coNo);

		model.addAttribute("commentView", commentDto); // 속성을 고대로 가지고 감
		model.addAttribute("listPage", page); // 속성을 고대로 가지고 감
		model.addAttribute("no", no); 

		return "cUpdate";
	}

	@PostMapping("/board/cUpdate") // 수정
	public String update(@RequestParam(value="coNo")Integer coNo,@RequestParam(value = "no") Integer no,
			@RequestParam(value = "password") String password, 
			@RequestParam(value = "content") String content,
			@RequestParam(value = "author") String author,@RequestParam(value = "listPage") int page
			){

		BoardDto boardView = this.boardService.boardView(no);
		CommentDto commentDto = this.commentService.commentView(coNo);
		
		commentDto.commentUpdate(author, password, "", content, LocalDateTime.now(),boardView.toEntity());

		this.commentService.update(commentDto);

		return "redirect:/board/view?listPage="+page + "&no="+no +"&or=desc#comment"+coNo;	
	}

	@GetMapping("/board/cDelete") // 삭제
	public String delete(@RequestParam(value = "no") Integer no,
			@RequestParam(value = "listPage") int page,
			@RequestParam(value = "coNo") Integer coNo) {

		this.commentService.delete(coNo);
		
		return "redirect:/board/view?listPage="+page+"&no="+no +"&or=desc&result=delete";
	}
	
}
