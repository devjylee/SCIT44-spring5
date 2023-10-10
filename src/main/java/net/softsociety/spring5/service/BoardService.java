package net.softsociety.spring5.service;

import java.util.ArrayList;
import java.util.HashMap;

import net.softsociety.spring5.domain.Board;
import net.softsociety.spring5.domain.Reply;
import net.softsociety.spring5.util.PageNavigator;

public interface BoardService {

	public int insert(Board board);

	public ArrayList<Board> list(PageNavigator navi, String type, String searchWord);
	
	public Board select(int boardnum);

	public Board read(int boardnum);

	public void update(Board board);

	public void delete(Board board);

	public PageNavigator getPageNavigator(int pagePerGroup, int countPerPage, int page, String type, String searchWord);

	public int insertReply(Reply reply);

	public ArrayList<Reply> readReply(int boardnum);

	public void deleteReply(Reply reply);

	public Reply selectReply(int replynum);

	public void updateReply(Reply reply);

	public void deleteCheck(HashMap<String, Object> map);

}
