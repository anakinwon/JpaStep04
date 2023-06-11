package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

//@RunAs(SpringRunner.class)   => JUnit5 사용 시 필요 없어짐
@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {
    @Autowired MemberJpaRepository memberJpaRepository;
    @Autowired TeamJpaRepository teamJpaRepository;
    @PersistenceContext EntityManager em;

    @Test
    @Rollback(value=false)
    public void testMember() {
        // Given
        Member member = new Member("Anakin_StringData_JPA2");
        Member savedMember = memberJpaRepository.save(member);

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
        teamJpaRepository.save(team);

        Member member1 = new Member("Anakin_1", 25, team);
        Member member2 = new Member("Anakin_2", 32, team);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);


        // When : 단건 조회 검사
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        // Then
        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        // 2건 입력 후, 2건 건수 검증하기.
        List<Member> all = memberJpaRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);

        // 1건 삭제 후,  1건 검증
        memberJpaRepository.delete(member1);
//        memberJpaRepository.delete(member2);
        long deleteCount = memberJpaRepository.count();
        Assertions.assertThat(deleteCount).isEqualTo(1);

        // 수정 후 검증
        member2.setUsername("Anakin_3");
        Assertions.assertThat(member2.getUsername()).isEqualTo("Anakin_3");

    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        // Given
        Team team = new Team("Team1");
        teamJpaRepository.save(team);

        Member member1 = new Member("Anakin_1", 25, team);
        Member member2 = new Member("Anakin_2", 32, team);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // When : 단건 조회 검사
        List<Member> memberList =  memberJpaRepository.findByUsernameAndAgeGreaterThan("Anakin_1", 20);

        // Then
        Assertions.assertThat(memberList.get(0).getUsername()).isEqualTo("Anakin_1");
        Assertions.assertThat(memberList.get(0).getAge()).isEqualTo(25);

    }

    @Test
    public void findByUsername() {
        // Given
        Member member1 = new Member("Anakin_1");
        Member member2 = new Member("Anakin_2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // When : createNamedQuery 조회 검사
        List <Member> result = memberJpaRepository.findByUsername("Anakin_2");
        Member findMember = result.get(0);

        // Then
        Assertions.assertThat(findMember).isEqualTo(member2);

    }

    @Test
    public void testPage() {
        // Given
        Team team = new Team("Team1");
        teamJpaRepository.save(team);
        for (int i=0; i<100;i++){
            memberJpaRepository.save(new Member("Anakin1_"+i, 10, team));
        }
        int age = 10;
        int offset = 21;
        int limit = 20;

        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);

        long totCount = memberJpaRepository.totalCount(age);
        System.out.println("\t totCount = " + totCount);
        for (Member member : members) {
            System.out.println("\t\t\t Page members = " + member);
        }

    }


    @Test
    @Rollback(false)
    public void testBulkUpdate() {
        // Given
        Team team = new Team("Team1");
        teamJpaRepository.save(team);
        for (int i=0; i<30; i++){
            memberJpaRepository.save(new Member("Anakin_"+i, 10+i, team));
        }
        int age = 15;
        //when
        int result = memberJpaRepository.bulkAgePlus(age);

    }

}