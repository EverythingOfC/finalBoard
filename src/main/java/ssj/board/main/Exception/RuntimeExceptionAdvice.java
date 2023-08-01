package ssj.board.main.Exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackages = {"ssj.board.main"})    // 실행 예외: 컴파일러가 예외처리를 요구하지 않음. 개발자가 따로 해줘야 함.
@Log4j2
public class RuntimeExceptionAdvice {


    @ExceptionHandler(NullPointerException.class)
    public String nullExcept(NullPointerException ex, Model model){

        log.error(ex.getMessage());
        model.addAttribute("message","null 참조 예외");
        return "error";
    }
}
