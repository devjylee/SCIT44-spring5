package net.softsociety.spring5.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.softsociety.spring5.dao.BoardDAO;
import net.softsociety.spring5.domain.Board;
import net.softsociety.spring5.domain.Reply;
import net.softsociety.spring5.util.PageNavigator;

@Transactional
@Service
public class BoardServiceImpl implements BoardService {
	
	@Autowired
	BoardDAO dao;
	
	//글 작성
	public int insert(Board board) {
		return dao.insert(board);
	}

	//글 목록
	@Override
	public ArrayList<Board> list(PageNavigator navi, String type, String searchWord) {
		HashMap<String, String> map = new HashMap<>();
		map.put("type", type);
		map.put("searchWord", searchWord);

		RowBounds rb = new RowBounds(navi.getStartRecord(),navi.getCountPerPage());
		return dao.list(map, rb);
	}

	//글 읽기
	@Override
	public Board read(int boardnum) {
		//조회수1 증가
		dao.updateHits(boardnum);
		Board board = dao.read(boardnum);
		return board;
	}
	
	//글 선택(수정 할 때)
	@Override
	public Board select(int boardnum) {
		Board board = dao.read(boardnum);
		return board;
	}

	//글 수정
	@Override
	public void update(Board board) {
		dao.updateBoard(board);
		return;
	}

	//글 삭제
	@Override
	public void delete(Board board) {
		dao.deleteBoard(board);
		return;
	}

	//페이지 정보 생성
	@Override
	public PageNavigator getPageNavigator(int pagePerGroup, int countPerPage, int page, String type,
			String searchWord) {
		//검색할 정보
		HashMap<String, String> map = new HashMap<>();
		map.put("type", type);
		map.put("searchWord", searchWord);
		
		//글 수(검색 포함)
		int total = dao.getTotal(map);
		
		//페이지 수, 글 수
		PageNavigator navi = new PageNavigator(pagePerGroup, countPerPage, page, total);
		
		return navi;
	}

	//댓글 작성
	@Override
	public int insertReply(Reply reply) {
		return dao.insertReply(reply);
	}

	//댓글 목록
	@Override
	public ArrayList<Reply> readReply(int boardnum) {
		return dao.readReply(boardnum);
	}
	
	//댓글 삭제
	@Override
	public void deleteReply(Reply reply) {
		dao.deleteReply(reply);
		return;
	}
	
	//댓글 선택(수정)
	@Override
	public Reply selectReply(int replynum) {
		Reply reply = dao.selectReply(replynum);
		return reply;
	}

	//댓글 수정
	@Override
	public void updateReply(Reply reply) {
		dao.updateReply(reply);
		return;
	}

	@Override
	public void deleteCheck(HashMap<String, Object> map) {
		dao.deleteCheck(map);
	}

}
