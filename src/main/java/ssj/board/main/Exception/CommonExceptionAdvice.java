package ssj.board.main.Exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice(basePackages = {"ssj.board.main"})    // 일반 예외(컴파일 체크 예외)는 컴파일러가 예외처리를 요구
public class CommonExceptionAdvice {

    @ExceptionHandler(value={IOException.class})  // 파일 입출력 오류
    public String ioExcept(Model model){

        model.addAttribute("message","파일을 읽어오는데 문제가 발생했습니다.");
        return "error";
    }
}
