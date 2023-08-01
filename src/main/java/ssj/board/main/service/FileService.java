package ssj.board.main.service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

	public void upload(MultipartFile[] files, BoardDto result) {

		if (files != null) { // 비어있지 않으면
			for (MultipartFile m : files) {

				if (!m.isEmpty()) {

					String fName = m.getOriginalFilename(); // 원본 파일 이름
					String sName = UUID.randomUUID() + "_" + fName; // 저장되는 파일 이름
					String contentType = m.getContentType(); // 파일의 MIME 타입
					long size = m.getSize(); // 파일 크기
					String savePath = System.getProperty("user.dir") + "/files/" + sName;

					try {
						// 지정된 경로에 해당하는 파일 객체를 생성한다.
						// 서버로 넘어온 파일의 내용을 지정된 파일 객체에 전송(복사)한다.
						m.transferTo(new File(savePath));
						FilePack filePack = new FilePack();
						filePack.setOriginalName(fName);
						filePack.setType(contentType);
						filePack.setSavedPath(savePath);
						filePack.setSize(size);
						filePack.setBoard(result.toEntity());
						filePack.setCreatedDate(LocalDateTime.now());

						this.fileRepository.save(filePack);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void download(@RequestParam(value = "savedPath") String savedPath, HttpServletResponse response) throws IOException { // 파일 다운로드

		File file = new File(savedPath);
		savedPath = savedPath.substring(savedPath.indexOf("_") + 1); // _와 _앞의 랜덤문자를 모두 자르고 나머지 파일명만 남김

		InputStream inputStream = new FileInputStream(file);
				OutputStream outputStream = response.getOutputStream();
			// 파일 다운로드를 위한 Response Header 설정
			response.setContentType("application/octet-stream"); // 응답데이터가 binary데이터

			// 서버에서 전송한 데이터가 브라우저에서 어떻게 처리될 지 나타냄, attchment: 해당 데이터를 파일로 다운로드하도록 함.
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + new String(savedPath.getBytes("UTF-8"), "ISO-8859-1") + "\""); // 다운받는
																												// 이름 지정

			// 1024byte씩 끊어서 읽거나 씀.
			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) > 0) { // 파일 데이터를 1024byte씩 읽어서 버퍼에 저장
				outputStream.write(buffer, 0, length); // 버퍼에 읽어들인 내용을 출력스트림에 쓴다.
			}
			outputStream.flush();
			outputStream.close();
			inputStream.close();
	}

	public void deleteFile(String dFile) {

		if (dFile.trim().length() > 0) {    // 삭제버튼이 클릭된 파일이 있으면
			String[] dFiles = dFile.trim().split(" ");    // fId들을 불러옴.

			for (String d : dFiles) {
				int fId = Integer.parseInt(d);
				Optional<FilePack> f = this.fileRepository.findById(fId);
				if (f.isPresent()) {
					String savedPath = f.get().getSavedPath();
					File file = new File(savedPath);
					if (file.exists())
						file.delete(); // 파일 경로에서 삭제
					this.fileRepository.deleteById(fId); // 파일 엔티티에서도 삭제
				}
			}
		}
	}

	public Integer countSize(Integer no) {
		return this.fileRepository.countSize(no);
	}

	public List<FilePack> viewFile(Integer no){
		return this.fileRepository.findByBoardId(no);
	}
}
