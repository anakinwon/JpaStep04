package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {
    @Autowired MemberJpaRepository memberJpaRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired TeamJpaRepository teamRepository;
    @Autowired MemberSpec memberSpec;

    @Autowired EntityManager em;

    @Test
    @Rollback(value=false)
    public void testMember() {
        // Given
        Member member = new Member("Anakin_StringData_JPA2");
        Member savedMember = memberRepository.save(member);

        // When
        Member findMember = memberJpaRepository.find(savedMember.getId());

        // Then
        Assertions.assertThat(findMember).isEqualTo(member);
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    }

    @Test
    @Rollback(value=false)
    public void basicCRUD() {
        // Given
        Team team = new Team("Team1");
        teamRepository.save(team);

        Member member1 = new Member("Anakin_1", 25, team);
        Member member2 = new Member("Anakin_2", 32, team);
        memberRepository.save(member1);
        memberRepository.save(member2);


        // When : 단건 조회 검사
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        // Then
        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        // 2건 입력 후, 2건 건수 검증하기.
        List<Member> all = memberRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);

        // 1건 삭제 후,  1건 검증
        memberRepository.delete(member1);
//        memberJpaRepository.delete(member2);
        long deleteCount = memberRepository.count();
        Assertions.assertThat(deleteCount).isEqualTo(1);

        // 수정 후 검증
        member2.setUsername("Anakin_3");
        Assertions.assertThat(member2.getUsername()).isEqualTo("Anakin_3");

    }

    @Test
    public void testFindByUsernameAndAgeGreaterThan() {
        // Given
        Team team = new Team("Team1");
        teamRepository.save(team);

        Member member1 = new Member("Anakin_1", 25, team);
        Member member2 = new Member("Anakin_2", 32, team);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // When : 단건 조회 검사
        List<Member> memberList =  memberRepository.findByUsernameAndAgeGreaterThan("Anakin_2", 30);

        // Then
        Assertions.assertThat(memberList.get(0).getUsername()).isEqualTo("Anakin_2");
        Assertions.assertThat(memberList.get(0).getAge()).isEqualTo(32);

    }

    @Test
    public void findByUsername() {
        // Given
        Member member1 = new Member("Anakin_1");
        Member member2 = new Member("Anakin_2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // When : createNamedQuery 조회 검사
        List <Member> result = memberRepository.findByUsername("Anakin_2");
        Member findMember = result.get(0);

        // Then
        Assertions.assertThat(findMember).isEqualTo(member2);

    }

    @Test
    public void testFindUser() {
        // Given
        Team team = new Team("Team1");
        teamRepository.save(team);

        Member member1 = new Member("Anakin_1", 25, team);
        Member member2 = new Member("Anakin_2", 32, team);

        memberRepository.save(member1);
        memberRepository.save(member2);

        // When : createNamedQuery 조회 검사
        List<Member> result = memberRepository.findUser("Anakin_1", 10);
//        Member findMember = result.get(0);

        // Then
        Assertions.assertThat(result.get(0)).isEqualTo(member1);

    }

    @Test
    public void testFindUserNameList() {
        // Given
        Team team = new Team("Team1");
        teamRepository.save(team);

        Member member1 = new Member("Anakin_1", 25, team);
        Member member2 = new Member("Anakin_2", 32, team);

        memberRepository.save(member1);
        memberRepository.save(member2);

        // When : createNamedQuery 조회 검사
        List<String> usernameList = memberRepository.findUsernameList();

        for (String s : usernameList) {
            System.out.println("s = " + s);
        }

        // Then
//        Assertions.assertThat(result.get(0)).isEqualTo(member1);

    }

    @Test
    public void testFindMemberDto() {
        // Given
        Team team = new Team("Team1");
        teamRepository.save(team);

        Member member1 = new Member("Anakin_1", 25, team);
        Member member2 = new Member("Anakin_2", 32, team);

        memberRepository.save(member1);
        memberRepository.save(member2);

        // When : createNamedQuery 조회 검사
        List<MemberDto> memberDto = memberRepository.findMemberDto();

        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }

    }


    @Test
    public void testFindUserName() {
        // Given
        Team team = new Team("Team1");
        teamRepository.save(team);

        Member member1 = new Member("Anakin_1", 25, team);
        Member member2 = new Member("Anakin_2", 32, team);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findMembers("akin", 10);

        for (Member member : result) {
            System.out.println("member = " + member);
        }

    }


    @Test
    public void testFindByNamesIn() {
        // Given
        Team team = new Team("Team1");
        teamRepository.save(team);

        Member member1 = new Member("Anakin_1", 25, team);
        Member member2 = new Member("Anakin_2", 32, team);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByNamesIn(Arrays.asList("Anakin_1","Anakin_2"));

        for (Member member : result) {
            System.out.println("member = " + member);
        }

    }

    @Test
    public void testPage() {
        // Given
        Team team = new Team("Team1");
        teamRepository.save(team);
        for (int i=0; i<30; i++){
            memberRepository.save(new Member("Anakin_"+i, 10, team));
        }

        int age = 10;
        //when
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC,"username"));
        Page<Member> page = memberRepository.findByAge(10, pageRequest);

        // 외부 공개 시 엔티티로 반환하면 안되며,
        // 페이지를 유지하면서 엔티티를 DTO로 반환하는 방법
        Page<MemberDto> tMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), m.getTeam().getName()));

        //then
        long totalElements = page.getTotalElements();
        System.out.println("\t totalElements = " + totalElements);

        List<Member> content = page.getContent(); //조회된 데이터
        for (Member member : content) {
            System.out.println("\t\t memberContent = " + member);
        }

        Assertions.assertThat(content.size()).isEqualTo(10);           //조회된 데이터 수
        Assertions.assertThat(page.getTotalElements()).isEqualTo(30);  //전체 데이터 수         // Page -> Slice 형태일 경우 값 없음
        Assertions.assertThat(page.getNumber()).isEqualTo(0);          //페이지 번호
        Assertions.assertThat(page.getTotalPages()).isEqualTo(3);      //전체 페이지 번호       // Page -> Slice 형태일 경우 값 없음
        Assertions.assertThat(page.isFirst()).isTrue();                        //첫번째 항목인가?
        Assertions.assertThat(page.hasNext()).isTrue();                        //다음 페이지가 있는가?

    }


    @Test
    @Rollback(false)
    public void testBulkUpdate() {
        // Given
        Team team = new Team("Team1");
        teamRepository.save(team);
        for (int i=0; i<30; i++){
            memberRepository.save(new Member("Anakin_"+i, 10+i, team));
        }
        int age = 25;
        //when
        int result = memberRepository.bulkAgeUpdate(age);

        // 벌크 작업 후에는 반드시 아래와 같이 초기화 해 준 후 후속작업을 해야 한다.
        // Repository에서 @Modifying(clearAutomatically = true) 옵션 사용 시 생략 가능.
        em.flush();
        em.clear();

        // flush() 와 Clear() 초기화를 해 주지 않으면 재조회 시 이전 데이터 35가 조회되고,
        // 초기화를 해 주면 재조회 시 업데이트가 된 350이 조회된다.
        List<Member> rltName = memberRepository.findByUsername("Anakin_25");
        System.out.println("\n\n\n rltName.get(0).getAge() = " + rltName.get(0).getAge());


    }

    @Test
    public void findMemeberLazy() {
        // Given
        // Anakin1 -> Team_A
        // Anakin2 -> Team_B

        Team teamA = new Team("Team_A");
        Team teamB = new Team("Team_B");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("Anakin1",25, teamA);
        Member member2 = new Member("Anakin2",32, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // When
        // select Member 객체만 가져온다.
        List<Member> members = memberRepository.findAll();
        // findMemberFetchJoin 지연 로딩 패치조인
        // 한방 쿼리로 모두 가져 온다.
//        List<Member> members = memberRepository.findMemberFetchJoin();
        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            // 지연로딩으로 인해서 가짜 객체를 가져온다.
            // findMemberFetchJoin 처리시 진짜 객체를 가져온다.
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }

    }

    @Test   // JPA Hint 테스트하기 (ReadOnly 속성 힌트 사용하기)
    public void queryHint() {
        Team team  = new Team("TEAM_A");
        teamRepository.save(team);

        Member member = new Member("Anakin1",10, team);
        memberRepository.save(member);
        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();

        // 찾아온 회원이름을 수정하면,
        findMember.setUsername("Anakin_Old");

        // 변경감지가 동작되어 Update 쿼리가 수행 예상.
        em.flush();
        em.clear();

        // 그러나, ReadOnly 속성의 JPA Hint를 사용하게 되면,
        Member findMember2 = memberRepository.findReadOnlyByUsername("Anakin_Old");

        // 찾아온 회원이름을 수정해도,
        findMember2.setUsername("Anakin_New");

        // 변경감지가 동작되지 않아서.
        // Update 쿼리가 수행되지 않는다.
        em.flush();

    }

    @Test   // JPA Lock 테스트하기 (조회된 데이터 보호하기.)   select ... for update 가 자동으로 쿼리 수행된다.
    public void queryLock() {
        Team team  = new Team("TEAM_A");
        teamRepository.save(team);

        Member member = new Member("Anakin1",10, team);
        memberRepository.save(member);
        em.flush();
        em.clear();

        Member findMember = memberRepository.findLockByUsername("Anakin1");

        // 찾아온 회원이름을 수정하면,
        findMember.setUsername("Anakin_Old");
    }

    @Test   // 사용자 정의 테스트
    public void queryCustom() {
        Team teamA = new Team("Team_A");
        Team teamB = new Team("Team_B");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("Anakin1",25, teamA);
        Member member2 = new Member("Anakin2",32, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        List<Member> result = memberRepository.findMemberCustom();
        for (Member member : result) {
            System.out.println("\t\t memberCustom = " + member);
        }

    }

    @Test   // Specifications (명세) 테스트  // 테스트 잘 안됨...
    public void testSpec() {
        // Given
        Team teamA = new Team("teamA");
        em.persist(teamA);
        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);

        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        //when
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        //then
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test   // Query By Example 테스트 // 조인에서 기술적 문제가 존재 함. Outer조인 불가능.  // 실무에선 QueryDSL을 사용해라.
    public void QueryByExampleTest() {
        // Given
        Team teamA = new Team("ExamTeam");
        em.persist(teamA);
        em.persist(new Member("Anakin1", 0, teamA));
        em.persist(new Member("Anakin2", 0, teamA));
        em.flush();

        //when
        //Probe 생성
        Member member = new Member("Anakin1");
        Team team = new Team("ExamTeam"); //내부조인으로 teamA 가능
        member.setTeam(team);

        //ExampleMatcher 생성, age 프로퍼티는 무시
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");
        Example<Member> example = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(example);

        //then
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test   // 단 하나의 컬럼만 가져오고 싶을때 유용한 테스트.
    public void testProjections() throws Exception {
        //given
        Team teamA = new Team("ProjectionTeamA");
        em.persist(teamA);
        Member m1 = new Member("ProjectionAnakin1", 0, teamA);
        Member m2 = new Member("ProjectionAnakin2", 0, teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        //when
        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("ProjectionAnakin1");

        //then
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test   // Open Projection 테스트하기 - 모두 다 가져오기.
    public void testOpenProjections() throws Exception {
        //given
        Team teamA = new Team("ProjectionTeamA");
        em.persist(teamA);
        Member m1 = new Member("ProjectionAnakin1", 23, teamA);
        Member m2 = new Member("ProjectionAnakin2", 24, teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        //when
        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("ProjectionAnakin2");

        for (UsernameOnly usernameOnly : result) {
            System.out.println("usernameOnly = " + usernameOnly.getUsername());
        }
        //then
        Assertions.assertThat(result.size()).isEqualTo(1);
    }


    @Test   // Open Projection 테스트하기 - 모두 다 가져오기.
    public void testOpenProjectionDto() throws Exception {
        //given
        Team teamA = new Team("ProjectionTeamA");
        em.persist(teamA);
        Member m1 = new Member("ProjectionAnakin1", 23, teamA);
        Member m2 = new Member("ProjectionAnakin2", 24, teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        //when
        List<UsernameOnlyDto> result = memberRepository.findProjectionDtoByUsername("ProjectionAnakin1");
        for (UsernameOnlyDto usernameOnlyDto : result) {
            System.out.println("usernameOnlyDto = " + usernameOnlyDto.getUsername());
        }
        //then
        Assertions.assertThat(result.size()).isEqualTo(1);
    }


    @Test   // <동적 프로젝션> 테스트
    public void testOpenProjectionDtoType() throws Exception {
        //given
        Team teamA = new Team("ProjectionTeamA");
        em.persist(teamA);
        Member m1 = new Member("ProjectionAnakin1", 23, teamA);
        Member m2 = new Member("ProjectionAnakin2", 24, teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        // When
        // 동적 프로젝션 : 제너릭 타입을 전송해서 사용 가능 (UsernameOnlyDto.class)
        List<UsernameOnlyDto> result = memberRepository.findProjectionDtoByUsername("ProjectionAnakin1", UsernameOnlyDto.class);
        for (UsernameOnlyDto usernameOnlyDto : result) {
            System.out.println("usernameOnlyDto = " + usernameOnlyDto.getUsername());
        }
        //then
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    /**
     *  <중첩 프로젝션> 테스트
     *      - Root   (메인) 쿼리는 최적화되어 필요한 컬럼만 가져오지만,
     *      - Branch (내부) 쿼리는 최적화 되지 못하고 모두 가져온다.
     *
     * */

    @Test
    public void testNestedClosedProjections() throws Exception {
        //given
        Team teamA = new Team("ProjectionTeamA");
        em.persist(teamA);
        Member m1 = new Member("ProjectionAnakin1", 23, teamA);
        Member m2 = new Member("ProjectionAnakin2", 24, teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        // When
        // 중첩 프로젝션 : 제너릭 타입을 전송해서 사용 가능 (NestedClosedProjections.class)
        List<NestedClosedProjections> result = memberRepository.findProjectionDtoByUsername("ProjectionAnakin1", NestedClosedProjections.class);
        for (NestedClosedProjections nestedClosedProjections : result) {
            System.out.println("\t\t\t getUsername = " + nestedClosedProjections.getUsername()); // Outer는 필요한 것만 가져온다.
            System.out.println("\t\t\t getTeam     = " + nestedClosedProjections.getTeam());     // Inner는 모두 가져온다.
        }

        //then
        Assertions.assertThat(result.size()).isEqualTo(1);
    }


    /**
     * <네이티브 쿼리>
     *     - 네이티브 쿼리 사용하기
     *     - 스프링 데이터 JPA 기반 네이티브 쿼리
     *     - 가급적 네이티브 쿼리는 사용하지 않는게 좋음, 정말 어쩔 수 없을 때 사용
     *     - 최근에 나온 궁극의 방법 : Spring-Data-Projections 활용
     *        1). 페이징 지원
     *        2). 반환 타입 :
     *             2.1) Object[]
     *             2.2) Tuple
     *             2.3) DTO(스프링 데이터 인터페이스 Projections 지원)
     *        3). 제약
     *             3.1) Sort 파라미터를 통한 정렬이 정상 동작하지 않을 수 있음(믿지 말고 직접 처리)
     *             3.2) JPQL처럼 애플리케이션 로딩 시점에 문법 확인 불가
     *             3.3) 동적 쿼리 불가
     *
     *
     * 네이티브 SQL을 DTO로 조회할 때는  => JdbcTemplate or myBatis 권장
     *
     * */
    @Test
    public void testNativeQuery() {
        //Given
        Team teamA = new Team("NativeTeamA");
        em.persist(teamA);
        Member m1 = new Member("NativeAnakin1", 23, teamA);
        Member m2 = new Member("NativeAnakin2", 24, teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        // When
        // 네이티브 쿼리 수행 테스트
        // 나이가 21 ~ 30 살 사이의 'kin' 이 포함된 이름 조회
        List<Member> result = memberRepository.findByNativeQuery("%kin%", 21, 30);

        for (Member member : result) {
            System.out.println("\t\t\t member.getUsername() = " + member.getUsername());
        }
    }

    /**
     * <Projections 활용>
     *     - 스프링 데이터 JPA 네이티브 쿼리 + 인터페이스 기반 Projections 활용
     *     - countQuery
     *     - nativeQuery
     *
     * */

    @Test
    public void testNativeProjection() {
        //Given
        Team teamA = new Team("NativeTeamA");
        em.persist(teamA);
        Member m1 = new Member("NativeAnakin1", 23, teamA);
        Member m2 = new Member("NativeAnakin2", 24, teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        // When
        // 네이티브 쿼리 수행 테스트
        // 나이가 21 ~ 50 살 사이의 'kin' 이 포함된 이름
        // 조회해서 1번째(2페이지) 부터 5개만 페이징 처리한다.
      Page<MemberProjection> result = memberRepository.findByNativeProjection("%kin%"
                                                                              , 21
                                                                              , 50
                                                                              , "%kin%"
                                                                              , 21
                                                                              , 50
                                                                              , PageRequest.of(1, 5)
                                                                             );

        for (MemberProjection memberProjection : result) {
            System.out.println("\t\t\t memberProjection.getUsername() = " + memberProjection.getUsername());
        }

    }
}