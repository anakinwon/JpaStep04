package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Duration;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

    @PersistenceContext EntityManager em;
    @Autowired MemberRepository memberRepository;

    @Test
    public void testEntity() {
        Team teamA = new Team("TEAM A");
        Team teamB = new Team("TEAM B");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("Anakin1", 25, teamA);
        Member member2 = new Member("Anakin2", 37, teamB);
        Member member3 = new Member("Padme3", 24, teamA);
        Member member4 = new Member("Padme4", 32, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);


        // 영속성 컨텍스트에 모아 놓은 쿼리를 강제로 모두 실행시킨다.
        em.flush();
        // 캐시 메모리 초기화하기...
        em.clear();

        //확인
       List<Member> members = em.createQuery("select m from Member m ", Member.class)
               .getResultList();

        for (Member member : members) {
            System.out.println("\t\t\tmember = " + member);
            System.out.println("\t\t\t\t -> member.Team = " + member.getTeam());
        }
    }

    @Test
    public void JpaEventBaseEntity() {
        Team teamA = new Team("TEAM A");
        Team teamB = new Team("TEAM B");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("Anakin1", 25, teamA);
        Member member2 = new Member("Anakin2", 37, teamB);
        Member member3 = new Member("Padme3", 24, teamA);
        Member member4 = new Member("Padme4", 32, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        // 영속성 컨텍스트에 모아 놓은 쿼리를 강제로 모두 실행시킨다.
        em.flush();
        // 캐시 메모리 초기화하기...
        em.clear();

        //확인
        List<Member> members = em.createQuery("select m from Member m ", Member.class)
                .getResultList();

        for (Member member : members) {
            System.out.println("\t\t\tmember = " + member);
            System.out.println("\t\t\t\t -> member.Team = " + member.getTeam());
        }
    }

    /**
     *  순수 JPA 방식 Auditing
     *  */
    @Test
    public void JpaEventBaseEntityAudition() throws InterruptedException {
        // Given
        Member member = new Member("순수JPA_ Auditing_Anakin");
        memberRepository.save(member);

        // 5초 후에 업데이트
        Thread.sleep(5000);
        member.setUsername("Editing_Anakin");

        em.flush();
        em.clear();

        // When
        Member findMember = memberRepository.findById(member.getId()).get();

        // Then
        System.out.println("findMember = " + findMember);
        System.out.println("\t\t 등록일시 = " + findMember.getCreatedDate());
        System.out.println("\t\t 수정일시 = " + findMember.getUpdatedDate());

        // 두 날짜의 차이 계산
        Duration diff = Duration.between(findMember.getCreatedDate(), findMember.getUpdatedDate());
        System.out.printf("시간: %d, 분: %d, 초: %d, 밀리초: %d, 나노초: %d",
                diff.toHours(), diff.toMinutes(), diff.getSeconds(), diff.toMillis(), diff.getNano());
    }

    /**
     *  Spring Data JPA 방식 Auditing
     *  */
    @Test
    public void JpaEventBaseEntityAudit() throws InterruptedException {
        // Given
        Member member = new Member("스프링데이터JPA_Auditing_Anakin");
        memberRepository.save(member);

        // 5초 후에 업데이트
        Thread.sleep(5000);
        member.setUsername("스프링데이터JPA_Editing_Anakin");

        em.flush();
        em.clear();

        // When
        Member findMember = memberRepository.findById(member.getId()).get();

        // Then
        System.out.println("findMember = " + findMember);
        System.out.println("\t\t 등록일시 = " + findMember.getCreatedDate());
        System.out.println("\t\t 수정일시 = " + findMember.getUpdatedDate());

        // 두 날짜의 차이 계산
        Duration diff = Duration.between(findMember.getCreatedDate(), findMember.getUpdatedDate());
        System.out.printf("시간: %d, 분: %d, 초: %d, 밀리초: %d, 나노초: %d",
                diff.toHours(), diff.toMinutes(), diff.getSeconds(), diff.toMillis(), diff.getNano());
    }

}