package ssj.board.main.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import ssj.board.main.dto.BoardDto;
import ssj.board.main.entity.FilePack;
import ssj.board.main.repository.FileRepository;

@Service
@RequiredArgsConstructor
public class FileService {

	private final FileRepository fileRepository;
	
	public void upload(MultipartFile[] files,BoardDto result) {
		
		for(MultipartFile m : files) {
			String fName = m.getOriginalFilename();		// 원본 파일 이름
			String sName = UUID.randomUUID() + "_" + fName;	// 저장되는 파일 이름
			String contentType = m.getContentType();	// 응답 형태
			long size = m.getSize();	// 파일 크기
			String savePath = "C:/practice/board/files/" + sName;	// 업로드되는 경로
			
			try {
				m.transferTo(new File(savePath));	// 서버 로컬 파일에 파일을 저장
				
				FilePack filePack = new FilePack();
				filePack.setOriginalName(fName);
				filePack.setSavedName(sName);
				filePack.setType(contentType);
				filePack.setSavedPath(savePath);
				filePack.setSize(size);
				filePack.setBoard(result.toEntity());
				filePack.setCreatedDate(LocalDateTime.now());
				
				this.fileRepository.save(filePack);
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void update(MultipartFile[] files,BoardDto result) {	// 수정할 예정
		
		for(MultipartFile m : files) {
			String fName = m.getOriginalFilename();		// 원본 파일 이름
			String sName = UUID.randomUUID() + "_" + fName;	// 저장되는 파일 이름
			String contentType = m.getContentType();	// 응답 형태
			long size = m.getSize();	// 파일 크기
			String savePath = "C:/practice/board/files/" + sName;	// 업로드되는 경로
			
			try {
				m.transferTo(new File(savePath));	// 서버 로컬 파일에 파일을 저장
				
				FilePack filePack = new FilePack();
				filePack.setOriginalName(fName);
				filePack.setSavedName(sName);
				filePack.setType(contentType);
				filePack.setSavedPath(savePath);
				filePack.setSize(size);
				filePack.setBoard(result.toEntity());
				filePack.setCreatedDate(LocalDateTime.now());
				
				this.fileRepository.save(filePack);
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void download(@RequestParam String fileName,
			HttpServletResponse response)throws Exception{
		
		 File file = new File("C:/practice/board/files/"+fileName);
		 fileName = fileName.substring(fileName.indexOf("_")+1);	// _와 _앞의 랜덤문자를 모두 자르고 나머지 파일명만 남김
		
		
		 try (InputStream inputStream = new FileInputStream(file);
		         OutputStream outputStream = response.getOutputStream()) {
		        // 파일 다운로드를 위한 Response Header 설정
		        response.setContentType("application/octet-stream"); // 응답데이터가 binary데이터
		        response.setHeader("Content-Disposition", "attachment; filename=\"" + 	// 데이터의 배치를 첨부파일의 형태로
		        new String(fileName.getBytes("UTF-8"),"ISO-8859-1") + "\"");	// 다운받는 이름 지정

		        // 1024byte씩 끊어서 읽거나 씀.
		        byte[] buffer = new byte[1024];
		        int length;
		        while ((length = inputStream.read(buffer)) > 0) {	// 데이터가 없을때까지 읽어들임
		            outputStream.write(buffer, 0, length);	// 버퍼에 읽어들인 내용을 출력스트림에 쓴다.
		        }
		        outputStream.flush();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }			// 한글 인코딩을 위한 처리
	}

	public void deleteFile(Integer fId) {

		this.fileRepository.deleteById(fId);
	}
	
}