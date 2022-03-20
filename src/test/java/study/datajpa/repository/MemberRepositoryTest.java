package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember(){
        Member member = new Member("memberAA");
        Member save = memberRepository.save(member);

        Member findMember = memberRepository.findById(save.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member); //같은 영속성 보장 !

    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건조회
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    public void findByUserAndAgeGreaterThen(){
        Member m1 = new Member("AAA", 10, null) ;
        Member m2 = new Member("AAA", 20, null) ;
        memberRepository.save(m1);
        memberRepository.save(m2);
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
    }

    @Test
    public void testNamedQuery(){
        Member m1 = new Member("AAA", 10, null) ;
        Member m2 = new Member("AAA", 20, null) ;
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member member =  result.get(0);
        assertThat(member).isEqualTo(m1);
    }

    @Test
    public void testQuery(){
        Member m1 = new Member("AAA", 10, null) ;
        Member m2 = new Member("AAA", 20, null) ;
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA",10);
        Member member =  result.get(0);
        assertThat(member).isEqualTo(m1);
    }

    @Test
    @DisplayName("회원 이름을 조회한다.")
    public void findUsernameList(){
        Member m1 = new Member("AAA", 10, null) ;
        Member m2 = new Member("AAA", 20, null) ;
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();

        for (String s : usernameList) {
            System.out.println("s =" + s);
        }
    }

    @Test
    @DisplayName("회원 DTO를 조회한다.")
    public void findMemberDto(){
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10) ;
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();

        for (MemberDto s : memberDto) {
            System.out.println("s =" + s);
        }
    }

    @Test
    @DisplayName("In 절로 조회한다")
    public void findByNames(){

        Member m1 = new Member("AAA", 10) ;
        Member m2 = new Member("BBB", 10) ;
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> memberDto = memberRepository.findByNames(Arrays.asList("AAA","BBB"));

        for (Member member : memberDto) {
            System.out.println("member =" + member);
        }
    }

    @Test
    @DisplayName("반환 타입")
    public void returnType(){

        Member m1 = new Member("AAA", 10) ;
        Member m2 = new Member("BBB", 10) ;
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> memberDto = memberRepository.findListByUsername("AAA");
        Member findMember = memberRepository.findMemberByUsername("AAA");
        Optional<Member> optional = memberRepository.findOptionalByUsername("AAA");

        //단건 조회 할떄
    }

    @Test
    @DisplayName("조회를 페이징 한다")
    public void paging() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));


        int age = 10;
        //1. 먼저 PageRequest를 만들어야한다.
        //0. 페이지에서 3개 가져오고 소팅은 따라서 뺴도된다. username 기준으로
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> toMap = page.map(member -> new MemberDto(
                member.getId(),
                member.getUsername(),
                null));

        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);  //페이지 번호도 가져올 수 있다.
        assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 갯수
        assertThat(page.isFirst()).isTrue();           //처음 페이지가 트루냐?
        assertThat(page.hasNext()).isTrue();           //다음 페이지가 존재하냐?
    }

    @Test
    @DisplayName("조회를 slice 페이징 한다")
    public void slicePaging() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));


        int age = 10;
        //1. 먼저 PageRequest를 만들어야한다.
        //0. 페이지에서 3개 가져오고 소팅은 따라서 뺴도된다. username 기준으로
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);
        //List<Member> page = memberRepository.findSliceByAge(age, pageRequest);
        //별다른 기능말고 리스트만 가져와 반환 타입 쓸수 있다.
        
        //then
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);  //페이지 번호도 가져올 수 있다.
        assertThat(page.isFirst()).isTrue();           //처음 페이지가 트루냐?
        assertThat(page.hasNext()).isTrue();           //다음 페이지가 존재하냐?
    }


    @Test
    public void bulkUpdate() {

        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);
        //em.flush();
        //em.clear();

        //then
        assertThat(resultCount).isEqualTo(3);  //현재 20살이상  3명
    }

    @Test
    public void findMemberLazy(){
        //given
        //member -> teamA
        //member -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamA);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when N + 1
        // select Member 1
        //List<Member> members = memberRepository.findMemberFetchJoin();
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member = "+ member.getUsername());
            System.out.println("member.teamClass = "+ member.getTeam().getClass());
            System.out.println("member team =" + member.getTeam().getName());
        }
    }
    
    @Test
    public void queryHint() {
        //given
        Member member = new Member("member1" ,10);
        memberRepository.save(member);
        em.flush();  //지우는건 아니고 동기화
        em.clear();  //여기가 날라가는 부분
        
        //when
//        Member findMember = memberRepository.findMemberByUsername(member.getId()).get();//주의 실무에서 get 바로 이렇게 호출하는것은 노노해
        Member findMember = memberRepository.findReadOnlyByUsername("member1");//스냅샷을 안찍고 변경감지를 무시한다.
        findMember.setUsername("member2");

        em.flush();
        //업데이트 변경감지 적용 객체

    }

    @Test
    public void lock() {
        //given
        Member member = new Member("member1" ,10);
        memberRepository.save(member);
        em.flush();  //지우는건 아니고 동기화
        em.clear();  //여기가 날라가는 부분

        //when
        List<Member> result = memberRepository.findLockByUsername("member1");

        em.flush();
        //업데이트 변경감지 적용 객체

    }

    @Test
    public void projections() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);

        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1");

        for (UsernameOnly usernameOnly : result) {
            System.out.println("usernameOnly: " + usernameOnly);
        }
    }

    @Test
    public void nativeQuery() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);

        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        System.out.println("result " + result);
    }



}