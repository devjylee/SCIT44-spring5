package net.softsociety.spring5.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.softsociety.spring5.dao.MemberDAO;
import net.softsociety.spring5.domain.Member;

@Service
@Slf4j
public class MemberServiceImpl implements MemberService {

	@Autowired
	MemberDAO dao;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public int insertMember(Member member) {
		String pw = passwordEncoder.encode(member.getMemberpw());
		
		log.debug("암호화 전 : {}", member.getMemberpw());
		log.debug("암호화 후 : {}", pw);
		
		member.setMemberpw(pw);
		
		return dao.insertMember(member);
	}

	@Override
	public boolean idcheck(String searchid) {
		return dao.selectOne(searchid)==null;
	}

	@Override
	public Member searchid(String username) {
		return dao.selectOne(username);
	}

	@Override
	public int updateMember(Member member) {
		String pw = passwordEncoder.encode(member.getMemberpw());
		member.setMemberpw(pw);
		return dao.updateMember(member);
	}

}
