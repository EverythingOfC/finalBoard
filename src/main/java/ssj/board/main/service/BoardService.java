package ssj.board.main.service;

import java.io.File;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ssj.board.main.dto.BoardDto;
import ssj.board.main.entity.Board;
import ssj.board.main.entity.FilePack;
import ssj.board.main.repository.BoardRepository;
import ssj.board.main.repository.FileRepository;

@Service
@RequiredArgsConstructor
public class BoardService {

	private final BoardRepository boardRepository;
	private final FileRepository fileRepository;
	


	public BoardDto create(BoardDto boardDto){ // 등록
		return this.boardRepository.save(boardDto.toEntity()).toDto();
	}
	
	
	public Page<BoardDto> boardList(int page, String or, int view,
			String detail, String search) { // 전체 조회
		
		Sort sort = or.equals("desc") ? Sort.by("orNo").descending() : Sort.by("orNo").ascending();	// 원글 정렬 기준
		sort = sort.and(Sort.by("grOr").ascending());
		
		
		Pageable pageable = PageRequest.of(page, view, sort); // 정렬 기준에 맞게 페이징
		
		if (detail.equals("title")) // 검색 조건
			return this.boardRepository.findAllByTitleContaining(pageable, search).map(Board::toDto);  // 제목 검색
		else
			return this.boardRepository.findAllByAuthorContaining(pageable, search).map(Board::toDto); // 작성자 검색

	}

	public BoardDto boardView(Integer no) {
		Optional<Board> board = this.boardRepository.findByNo(no);
		return board.isPresent() ? board.get().toDto() : null;
	}

	public boolean badWordFilter(String word) {
		BadWord badWord = new BadWordImpl();

		return badWord.test(word) ? true : false; // 정상 단어이면 true, 비속어 or 선정적인 언어면 false
	}

    @Transactional
    public void deleteBoard(Integer no) throws Exception {	// 게시글 삭제 시, 해당 게시글의 파일을 삭제
     
        List<FilePack> files = this.fileRepository.findByBoardId(no);	// 게시글의 고유 번호로 파일을 찾음.

        for (FilePack f : files) {	// 해당 게시글의 파일들을 순회
        	File file = new File(f.getSavedPath());	// 저장된 경로의 파일을 불러옴.
            if (file.exists()){	// 존재하면
                file.delete();	// 삭제
            }
        }
        BoardDto boardView = boardView(no);	// 게시글 가져옴.
        
        // 삭제된 게시글 처리
        boardView.setAuthor("");
        boardView.setTitle("");
        boardView.setContent("");
        boardView.setPassword("");
        boardView.setRemoveC(true);
        boardView.getFilePacks().clear();	        
        boardView.setCList(null);
        
        this.boardRepository.save(boardView.toEntity());
  } 
    public int removeCount() {	// 삭제된 게시글 카운트
    	return this.boardRepository.removeCount();
    }
	
	public void updateGr(Integer no, Integer orNo,Integer grOr) {	// 답글을 하나 추가하면, 원글의 그룹번호보다 높은 번호들을 모두 1증가
		this.boardRepository.updateGr(no,orNo, grOr);
	}

	public void orGrCheck(BoardDto result,BoardDto check){
		
		if(check == null) {	// 새글
			result.setOrNo(result.getNo());	// 게시글 고유 번호로 그룹 번호를 초기화
			create(result);		// 원글 등록
			
		}else {				// 답글
			result.setOrNo(check.getOrNo());		// 원글의 그룹 번호로 초기화
			result.setGrOr(check.getGrOr());		// 원글의 그룹 순서로 초기화
			result.setGrDepth(check.getGrDepth()+1);// 깊이 증가
			create(result);				// 답글 등록
			updateGr(result.getNo(), result.getOrNo(), result.getGrOr());
		}
	}
	
	public void saveCsv(HttpServletResponse response) throws Exception {    // 엑셀 파일로 저장

        List<Board> list = this.boardRepository.findAll();

        String[] menu = {"번호", "제목", "작성자", "내용", "작성일" ,
        		"그룹 번호","그룹 순서","답변의 깊이"};   // Header로 사용할 column들
        String fileName = "download.xlsx";
	    Workbook workbook = new XSSFWorkbook();	// 엑셀 파일을 생성하기 위한 Workbook 객체 생성
	    Sheet sheet = workbook.createSheet("게시판"); // 엑셀 파일을 생성하기 위한 Workbook 객체 생성
	    sheet.setColumnWidth(0, 2000);
	    sheet.setColumnWidth(1, 20000);
	    sheet.setColumnWidth(2, 20000);
	    sheet.setColumnWidth(3, 50000);
	    
	    // Header에 Menu들을 추가
	    Row headerRow = sheet.createRow(0);
	    for(int i=0;i<menu.length;i++) {	
	    	headerRow.createCell(i).setCellValue(menu[i]);
	    }
	    
	    // Body에 데이터들을 추가
	    for(int i=0;i<list.size();i++) {	
	    	Row dataRow = sheet.createRow(i+1);	// 엑셀 행 생성
	    	BoardDto boardDto = list.get(i).toDto();	// 엑셀 행 데이터 생성
	    		dataRow.createCell(0).setCellValue(i+1);
	    		dataRow.createCell(1).setCellValue(boardDto.getTitle());
	    		dataRow.createCell(2).setCellValue(boardDto.getAuthor());
	    		dataRow.createCell(3).setCellValue(boardDto.getContent());
	    		dataRow.createCell(4).setCellValue(boardDto.getWriteDate());
	    		dataRow.createCell(5).setCellValue(boardDto.getOrNo());
	    		dataRow.createCell(6).setCellValue(boardDto.getGrOr());
	    		dataRow.createCell(7).setCellValue(boardDto.getGrDepth());
	    }
	
	    // 엑셀 파일 다운로드를 위한 설정
	    response.setContentType("application/vnd.ms-excel");
	    response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
	    
	    // 엑셀 파일 출력
	    workbook.write(response.getOutputStream());
	    
	    // Workbook 객체와 스트림 닫기
	    workbook.close();
	    response.getOutputStream().flush();
	    response.getOutputStream().close();
      
    }
	
}
