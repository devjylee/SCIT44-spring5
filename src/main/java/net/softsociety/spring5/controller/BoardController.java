package net.softsociety.spring5.controller;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import net.softsociety.spring5.domain.Board;
import net.softsociety.spring5.domain.Reply;
import net.softsociety.spring5.service.BoardService;
import net.softsociety.spring5.util.FileService;
import net.softsociety.spring5.util.PageNavigator;

@Slf4j
@RequestMapping("board")
@Controller
public class BoardController {
	
	@Autowired
	BoardService service;
	
	//파일 저장경로
	@Value("${spring.servlet.multipart.location}")
	String uploadPath;
	
	//게시판 목록의 페이지당 글 수
	@Value("${user.board.page}")
	int countPerPage;
	
	//게시판 목록의 페이지 이동 링크 수
	@Value("${user.board.group}")
	int pagePerGroup;
	
	//글 목록
	@GetMapping("list")
	public String list(Model model
			, String type
			, String searchWord
			, @RequestParam (name="page", defaultValue="1") int page) {
		/* log.debug("검색대상: {}, 검색어: {}", type, searchWord); */
		
		PageNavigator navi = service.getPageNavigator(pagePerGroup, countPerPage, page, type, searchWord);
		
		ArrayList<Board> list = service.list(navi, type, searchWord);

		model.addAttribute("list", list);
		model.addAttribute("navi", navi);
		model.addAttribute("type", type);
		model.addAttribute("searchWord", searchWord);
		
		return "boardView/list";
	}
	
	//글쓰기 폼
	@GetMapping("write")
	public String write() {
		return "boardView/write";
	}

	//글쓰기
	@PostMapping("write")
	public String write(
			@AuthenticationPrincipal UserDetails user
			, Board board
			, MultipartFile upload) {
		
		/* log.debug("저장할 글 정보: {}" , board); */
		
		if (upload != null && !upload.isEmpty()) {
			String savedfile=FileService.saveFile(upload, uploadPath);
			board.setOriginalfile(upload.getOriginalFilename());
			board.setSavedfile(savedfile);
		}
		
		/* log.debug("저장할 글정보: {}", board); */
		
		board.setMemberid(user.getUsername());
		service.insert(board);
		return "redirect:/board/list";
	}
	
	//글 읽기
	@GetMapping("read")
	public String read(
			@RequestParam(name="boardnum", defaultValue="0") int boardnum
			, Model model) {
		Board board = service.read(boardnum);
		ArrayList<Reply> reply = service.readReply(boardnum);
		
		/* log.debug("{}", reply); */
		
		if(board==null) {
			return "redirect:/board/list"; //글이 없으면 목록으로
		}
		model.addAttribute("board", board);
		model.addAttribute("reply", reply);
		return "boardView/read";
	}
	
	//글 수정
	@GetMapping("update")
	public String update(@RequestParam(name="boardnum", defaultValue="0") int boardnum, Model model) {
		Board board = service.select(boardnum);
		model.addAttribute("board", board);
		return "boardView/update";
	}
	
	@PostMapping("update")
	public String update(Board board
			, @AuthenticationPrincipal UserDetails user
			, MultipartFile upload) {
		/* log.debug("수정할 글 정보 : {}", board); */
		
		if(board.getOriginalfile() != null && !board.getOriginalfile().isEmpty() && upload != null && !upload.isEmpty()) {
			String fullPath = uploadPath + "/" + board.getSavedfile();
			FileService.deleteFile(fullPath);
		}
		
		if (upload != null && !upload.isEmpty()) {
			String savedfile=FileService.saveFile(upload, uploadPath);
			board.setOriginalfile(upload.getOriginalFilename());
			board.setSavedfile(savedfile);
		}
		
		board.setMemberid(user.getUsername());
		service.update(board);
		
		return "redirect:/board/read?boardnum=" + board.getBoardnum();
	}
	
	//글 삭제
	@GetMapping("delete")
	public String delete(int boardnum, @AuthenticationPrincipal UserDetails user) {
		//해당 번호의 글 정보 조회
		Board board = service.read(boardnum);
		if(board == null) {
			return "redirect:/board/list";
		}
		
		//Board에 파일이 있으면 삭제
		if (board.getSavedfile() != null && !board.getSavedfile().isEmpty()) {
			String fullPath = uploadPath + "/" + board.getSavedfile();
			FileService.deleteFile(fullPath);
		}
		
		//로그인 아이디를 Board 객체에 저장
		board.setMemberid(user.getUsername());
		service.delete(board);
		return "redirect:list";
	}
	
	//파일 다운로드
	@GetMapping("download")
	public void download(int boardnum
			, HttpServletRequest request
			, HttpServletResponse response) {
		
		/* log.debug("요청을 보낸 쪽의 아이피: {}",request.getRemoteAddr()); */
		
		//해당 글의 첨부파일명 확인
		Board board= service.select(boardnum);
		
		//파일의 경로를 이용해서 FileInputStream 객체를 생성
		String fullPath = uploadPath + "/" + board.getSavedfile();
		
		//response를 통해 파일 전송
		try {
			response.setHeader("Content-Disposition", " attachment;filename="+ URLEncoder.encode(board.getOriginalfile(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			FileInputStream in = new FileInputStream(fullPath);
			ServletOutputStream out = response.getOutputStream();
			
			FileCopyUtils.copy(in, out);
			
			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	//댓글 작성
	@PostMapping("insertReply")
	public String insertReply(
			@AuthenticationPrincipal UserDetails user
			, Reply reply) {
		reply.setMemberid(user.getUsername());
		service.insertReply(reply);
		return "redirect:/board/read?boardnum=" + reply.getBoardnum();
	}
	
	//댓글 삭제
	@GetMapping("deleteReply")
	public String deleteReply(
			@AuthenticationPrincipal UserDetails user
			, int replynum
			, int boardnum) {
		Reply reply = new Reply();
		reply.setReplynum(replynum);
		reply.setMemberid(user.getUsername());
		service.deleteReply(reply);
		return "redirect:/board/read?boardnum=" + boardnum;
	}
	
	//댓글 수정
	@GetMapping("updateReply")
	public String updateReply(@RequestParam(name="replynum", defaultValue="0") int replynum
			, Model model) {
		Reply reply = service.selectReply(replynum);
		model.addAttribute("reply", reply);
		return "boardView/updateReply";
	}
	
	@PostMapping("updateReply")
	public String updateReply(
			@AuthenticationPrincipal UserDetails user
			, Reply reply
			, int boardnum) {
		reply.setMemberid(user.getUsername());
		service.updateReply(reply);
		return "redirect:/board/read?boardnum=" + boardnum;
	}
	
	//관리자
	//글 목록
	@GetMapping("listAdmin")
	public String listAdmin(Model model
			, String type
			, String searchWord
			, @RequestParam (name="page", defaultValue="1") int page) {
		
		/* log.debug("검색대상: {}, 검색어: {}", type, searchWord); */
		 		
		PageNavigator navi = service.getPageNavigator(pagePerGroup, countPerPage, page, type, searchWord);
		
		ArrayList<Board> list = service.list(navi, type, searchWord);

		model.addAttribute("list", list);
		model.addAttribute("navi", navi);
		model.addAttribute("type", type);
		model.addAttribute("searchWord", searchWord);
		
		return "boardView/listAdmin";
	}
	
	//글 삭제
	@PostMapping("deleteAdmin")
	public String deleteAdmin(
			@AuthenticationPrincipal UserDetails user
			, String[] deletenum) {
		
		for(String num : deletenum) {
			log.debug("삭제할 글 번호:{}", num);
		}
		
		HashMap<String, Object> map = new HashMap<>();
		map.put("memberid", user.getUsername());
		map.put("deletenum", deletenum);
		
		service.deleteCheck(map);
		
		return "redirect:listAdmin";
	}
	
}
