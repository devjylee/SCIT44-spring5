package net.softsociety.spring5.service;

import net.softsociety.spring5.domain.Member;

public interface MemberService {

	public int insertMember(Member member);

	public boolean idcheck(String searchid);

	public Member searchid(String username);
	
	public int updateMember(Member member);
	
}
