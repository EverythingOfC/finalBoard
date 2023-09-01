package ssj.board.main.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
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
public class BoardService {		// 기능을 확장할 가능성이 있다면 인터페이스를 도입해야 한다.

	private final BoardRepository boardRepository;
	private final FileRepository fileRepository;
	
	public BoardDto create(BoardDto boardDto){ // 등록
		BoardDto bDto = BoardDto.builder().author(boardDto.getAuthor()).password(boardDto.getPassword()).title(boardDto.getTitle()).content(boardDto.getContent())
				.writeDate(LocalDateTime.now()).orNo(0).grOr(0).grDepth(0).parentNo(0).parentOr(0).recommand(0).views(0).build();	// 얻어온 값으로 초기화

		return this.boardRepository.save(bDto.toEntity()).toDto();
	}

	public BoardDto update(BoardDto boardDto){	// 수정
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

	public void increaseRecommend(Integer no){
		this.boardRepository.increaseRecommend(no);
	}

	public boolean increaseViews(Integer no,Long time,Long currentTime){
		if(time == null || currentTime - time > 3600000)	// 마지막 조회 후에 1시간이 지나면 조회 수 증가
		{
			this.boardRepository.increaseViews(no);
			return true;
		}
		return false;
	}

    @Transactional
    public void deleteBoard(Integer no) {	// 게시글 삭제 시, 해당 게시글의 파일을 삭제
     
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
    public Integer removeCount(String detail,String search) {	// 삭제된 게시글 카운트
    	if(detail.equals("title"))
    		return this.boardRepository.removeCountT(search);
    	else
    		return this.boardRepository.removeCountA(search);
    }
	
	public void updateGr(Integer no, Integer orNo,Integer grOr) {	// 답글을 하나 추가하면, 원글의 그룹번호보다 높은 번호들을 모두 1증가
		this.boardRepository.updateGr(no,orNo, grOr);
	}

	public void orGrCheck(BoardDto result,BoardDto check){	// 원글 답글 체크
		
		if(check == null) {	// 새글
			result.setOrNo(result.getNo());		// 게시글 고유 번호로 그룹 번호를 초기화
			Integer oCount = this.boardRepository.oCount();	// 원글의 개수
			result.setRelation(String.valueOf(oCount));	// 원글의 개수만큼 번호를 매김
			this.boardRepository.save(result.toEntity());	// 원글 등록
			
		}else {				// 답글
			result.setOrNo(check.getOrNo());		// 원글의 그룹 번호로 초기화
			result.setGrOr(check.getGrOr());		// 원글의 그룹 순서로 초기화
			result.setGrDepth(check.getGrDepth()+1);// 깊이 증가
			result.setParentNo(check.getNo());	// 부모글의 일련번호로 초기화
			
			Integer number = this.boardRepository.parentNoCount(result.getParentNo());	// 부모글에 있는 답글의 수
			result.setParentOr(number+1);	// 답글의 수로 번호를 매김
			String relation = check.getRelation() + "-" + result.getParentOr();	// 부모글의 관계 - 부모글에서 파생된 답글의 수 
			result.setRelation(relation);
			this.boardRepository.save(result.toEntity());	// 답글 등록
			updateGr(result.getNo(), result.getOrNo(), result.getGrOr());
		}
	}
	
	public void saveCsv(HttpServletResponse response,String or,String detail,String search) throws IOException, IllegalStateException {    // 엑셀 파일로 저장

		Sort sort = or.equals("desc") ? Sort.by("orNo").descending() : Sort.by("orNo").ascending();	// 원글 정렬 기준
		
		sort = sort.and(Sort.by("grOr").ascending());
		
		List<Board> list;
		if(detail.equals("title"))
			list = this.boardRepository.findAllByTitleContaining(sort,search);
		else
			list = this.boardRepository.findAllByAuthorContaining(sort,search);
		
        String[] menu = {"번호","제목","작성자","작성일"};   // Header로 사용할 column들
        String fileName = "writing_"+or+".xlsx";
	    Workbook workbook = new XSSFWorkbook();	     // 엑셀 파일을 생성하기 위한 Workbook 객체 생성
	    Sheet sheet = workbook.createSheet("게시판"); 
	    
	    CellStyle headerStyle = workbook.createCellStyle();	// 엑셀의 헤더 스타일을 설정함.
	    headerStyle.setAlignment(HorizontalAlignment.CENTER);
	    
	    sheet.setColumnWidth(0, 4000);
	    sheet.setColumnWidth(1, 18000);
	    sheet.setColumnWidth(2, 5500);
	    sheet.setColumnWidth(3, 5500);

	    // Header에 Menu들을 추가
	    Row headerRow = sheet.createRow(0);
	    
	    for(int i=0;i<menu.length;i++) {	
	    	Cell cell = headerRow.createCell(i);
	    	cell.setCellValue(menu[i]);
	    	cell.setCellStyle(headerStyle);	// header는 가운데 정렬
	    }
	    
	    CellStyle dateCellStyle = workbook.createCellStyle();	// 엑셀의 날짜 스타일을 설정함.
	    dateCellStyle.setAlignment(HorizontalAlignment.RIGHT);
	    
	    int size = list.size();
	    // Body에 데이터들을 추가
	    for(int i=0;i<size;i++) {	
	    	Row dataRow = sheet.createRow(i+1);			// 엑셀 행 생성
	    	BoardDto boardDto = list.get(i).toDto();	// 엑셀 행 데이터 생성
	    	String dateFormat = boardDto.getWriteDate().format(DateTimeFormatter.ofPattern("yyyy. M. d. hh:mm:ss"));	    	
	    	String re = "";
	    	
	    	for(int j=0;j<boardDto.getGrDepth();j++) {	// 답글의 깊이만큼 공백을 대입
	    		re += "    ";
	    	}
	    	
	    	dataRow.createCell(0).setCellValue(re + boardDto.getRelation());
	    	if(re.equals("")) {
	    		if(boardDto.getRemoveC() != null) 
    				dataRow.createCell(1).setCellValue("삭제된 메시지입니다.");
    			else {
    				dataRow.createCell(1).setCellValue(boardDto.getTitle() + " (" + boardDto.getFilePacks().size() + ") ");
    				dataRow.createCell(2).setCellValue(boardDto.getAuthor());
    			}
	    	}else {
	    		re += "[RE] ";
	    		if(boardDto.getRemoveC() != null) 
    				dataRow.createCell(1).setCellValue(re+"삭제된 메시지입니다.");
    			else {
    				dataRow.createCell(1).setCellValue(re+boardDto.getTitle() + " (" + boardDto.getFilePacks().size() + ") ");
    				dataRow.createCell(2).setCellValue(boardDto.getAuthor());
    			}
	    	}
			Cell cell = dataRow.createCell(3);
			cell.setCellValue(dateFormat);
			cell.setCellStyle(dateCellStyle);

	    }
	    		
	    // 응답에 대한 MIME타입을 설정(해당 데이터가 Microsoft의 엑셀 문서임을 나타냄)
	    response.setContentType("application/vnd.ms-excel");	
	    
	    // 서버에서 전송한 데이터가 브라우저에서 어떻게 처리될 지 나타냄, attchment: 해당 데이터를 파일로 다운로드하도록 함.
	    response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
	    
	    // 엑셀 파일에 데이터를 기록
	    workbook.write(response.getOutputStream());
	    
	    // 출력 스트림에 남은 데이터를 모두 출력
	    response.getOutputStream().flush();
	    
	    
	    // Workbook 객체와 스트림 닫기
	    workbook.close();	    
	    // HTTP응답을 보내기 전, 출력 버퍼에 저장된 데이터를 클라이언트에게 전송, 전송 완료시 버퍼를 비움.
	    response.getOutputStream().close();
      
    }

}
