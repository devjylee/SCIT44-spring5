package net.softsociety.spring5.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;
import net.softsociety.spring5.domain.Member;
import net.softsociety.spring5.service.MemberService;

//회원정보 관련 콘트롤러
@Slf4j
@RequestMapping("member")
@Controller
public class MemberController {
	
	@Autowired
	MemberService service;
	
	//회원 가입 폼으로 이동
	@GetMapping("join")
	public String join() {
		return "memberView/join";
	}
	
	//회원 가입 처리
	@PostMapping("join")
	public String join(Member m) {
		log.debug("{}" , m);
		service.insertMember(m);
		return "redirect:/";
	}
	
	//ID중복확인 폼
	@GetMapping("idcheck")
	public String idcheck() {
		return "memberView/idcheck";
	}
	
	//ID중복확인 처리
	@PostMapping("idcheck")
	public String idcheck(String searchid, Model model) {
		boolean result = service.idcheck(searchid);
		model.addAttribute("searchid", searchid);
		model.addAttribute("result", result);
		return "memberView/idcheck";
	}
	
	//로그인 폼으로 이동
	@GetMapping("loginForm")
	public String loginForm() {
		return "memberView/loginForm";
	}
	
	//수정 폼으로 이동
	@GetMapping("updateForm")
	public String updateForm(@AuthenticationPrincipal UserDetails user, Model model) {
		//로그인한 아이디로 회원정보 검색
		Member member = service.searchid(user.getUsername());
		//검색결과를 Model에 저장
		model.addAttribute("member", member);
		//HTML로 포워딩
		return "memberView/updateForm";
	}
	
	//수정 처리
	@PostMapping("update")
	public String update(@AuthenticationPrincipal UserDetails user, Member member) {
		log.debug("수정할 값 : {}", member);
		//로그인한 아이디를 member 객체에 추가
		member.setMemberid(user.getUsername());
		//member 객체를 서비스로 전달하여 DB 수정
	    service.updateMember(member);
		//메인화면으로 리다이렉트
		return "redirect:/";
	}
	
}
