package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;
import study.datajpa.repository.MemberRepository;
import study.datajpa.repository.TeamRepository;

import javax.annotation.PostConstruct;


/**
 * Web 확장 - 도메인 클래스 컨버터
 *    - HTTP 파라미터로 넘어온 엔티티의 아이디로 엔티티 객체를 찾아서 바인딩
 *    - 강사입장에서 실무에 권장하지는 않는다.
 *    - 간단한 예제에서는 사용가능하지만, 조금 복잡해 지면 사용이 어렵다.
 *    - 도메인 클래스 컨버터로 엔티티를 파라미터로 받으면 조회 용도로만 사용되고, 입력/수정은 안된다.
 *
 */

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    // Web 확장 - 도메인 클래스 컨버터  ==================================================
    // 도메인 클래스 컨버터 사용 전
    @GetMapping("/member1/{id}")
    public String findMember1(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    // 도메인 클래스 컨버터 사용 후
    @GetMapping("/member2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    // WEB서버 로딩 후 자동 생성
    @PostConstruct
    public void init() {
        // 테스트데이터 단건 입력.
//        memberRepository.save(new Member("ClassConverter_Anakin"));

        Team teamA = teamRepository.save(new Team("Team_Page"));

        // 테스트데이터 100건 입력.
        for(int i=0; i<100; i++) {
            memberRepository.save(new Member("Page_Anakin_"+i, 10+i, teamA));
        }
    }
    // Web 확장 - 도메인 클래스 컨버터  ==================================================

    /**
     * <Web 확장 - 페이징과 정렬>
     *     - 스프링 데이터가 제공하는 페이징과 정렬 기능을
     *     - 스프링 MVC에서 편리하게 사용할 수 있다.
     *     - http://localhost:8080/members                                    => JSON 전체데이터 출력 됨.
     *     - http://localhost:8080/members?page=3&size=10&sort=username,desc  => 이름 역순으로 3번째 페이지에서 10개 조회하기.
     *     - 페이지 사이즈 기본값 설정
     *        :글로벌 설정 = application.yml
     * */
    // 보안 상 Entity를 반환하는 것은 금지.
//    @GetMapping("/members")
//    public Page<Member> list(@PageableDefault(size=5) Pageable pageable) {  // @PageableDefault(size=5) : 기본값을 여기서 별도로 사용할 수 있다.
//        Page<Member> page = memberRepository.findAll(pageable);
//        return page;
//    }

    // Page 내용을 DTO로 변환하기
    //    - 엔티티를 API로 노출하면 다양한 문제가 발생한다.
    //    - 그래서 엔티티를 꼭 DTO로 변환해서 반환해야 한다.
    // 보안 상 DTO반환으로 변경할 것.
//    @GetMapping("/members")
//    public Page<MemberDto> list(@PageableDefault(size=5) Pageable pageable) {  // @PageableDefault(size=5) : 기본값을 여기서 별도로 사용할 수 있다.
//        Page<Member> page = memberRepository.findAll(pageable);
//        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), member.getTeam().getName()));
//        return map;
//    }

    // DTO를 축약해서 코드를 보다 간결하게 만들 수 있다.
    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size=5) Pageable pageable) {  // @PageableDefault(size=5) : 기본값을 여기서 별도로 사용할 수 있다.
//        Page<Member> page = memberRepository.findAll(pageable);
//        //Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), member.getTeam().getName()));
//        // 축약 가능
//        //Page<MemberDto> map = page.map(member -> new MemberDto(member));
//        // 메소드 레퍼런스로 최종 축약 가능
//        Page<MemberDto> map = page.map(MemberDto::new);
//        return map;
        return memberRepository.findAll(pageable)
                .map(MemberDto::new);
    }



}
