package net.softsociety.spring5.dao;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import net.softsociety.spring5.domain.Board;
import net.softsociety.spring5.domain.Reply;
import net.softsociety.spring5.util.PageNavigator;

@Mapper
public interface BoardDAO {
	
	public int insert(Board board);

	public ArrayList<Board> list(HashMap<String, String> map, RowBounds rb);

	public int getTotal(HashMap<String, String> map);
	
	public Board read(int boardnum);

	public void updateHits(int boardnum);

	public void updateBoard(Board board);

	public void deleteBoard(Board board);

	public int insertReply(Reply reply);

	public ArrayList<Reply> readReply(int boardnum);

	public void deleteReply(Reply reply);

	public Reply selectReply(int replynum);

	public void updateReply(Reply reply);

	public void deleteCheck(HashMap<String, Object> map);

}