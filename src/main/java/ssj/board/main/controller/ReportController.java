package ssj.board.main.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ssj.board.main.dto.ReportDto;
import ssj.board.main.service.ReportService;

@Controller
@RequiredArgsConstructor
public class ReportController {
    
    private final ReportService reportService;

    @PostMapping("/report/create")
    public String reportCreate(@ModelAttribute ReportDto reportDto,@RequestParam(value="coNo") Integer coNo){

        this.reportService.reportRegister(reportDto,coNo);

        return "redirect:/board/list";
    }

    @GetMapping("/report/delete")
    public String reportDelete(@RequestParam(value="rNo")Integer rNo){

        return "redirect:/board/list";
    }

    @GetMapping("/reportForm")
    public String cReportForm(Model model, @RequestParam(value = "coNo") Integer coNo) {

        model.addAttribute("coNo", coNo);

        return "cReport";
    }
}
