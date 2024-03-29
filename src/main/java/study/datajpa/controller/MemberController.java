package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/member2/{id}")
    public String findMember(@PathVariable("id")Member member){
        return  member.getUsername();
    }
    @GetMapping("/member")      //sort = "username"
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {
        //PagingAndSortingRepository 제공
        PageRequest.of(1,2);

        Page<MemberDto> map = memberRepository.findAll(pageable)
                //.map(member -> new MemberDto(member));
                .map(MemberDto::new);


        return map;
    }

    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user"+i, i));

        }

    }
}
