package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    // 저장하기
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    // 삭제하기
    public void delete(Member member) {
        em.remove(member);
    }

    // JPQL 전체 조회하기
    public List<Member> findAll() {
        List<Member> result = em.createQuery("select m from Member m ", Member.class)
                .getResultList();
        return result;
    }

    // Optional 조회, Member 결과가 null일 수 있다.
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    // 단건 조회,, 전체 건수 조회(getSingleResult())
    public Long count() {
        Long singleResult = em.createQuery("select count(m) from Member m ", Long.class)
                .getSingleResult();
        return singleResult;
    }

    // JPA 단건 조회
    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    // 1. 쿼리 메소드 : 메소드 이름으로 쿼리 생성
    //    이 기능도 실무에서 거의 사용하지 않는다.
    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
        return em.createQuery("select m from Member m where m.username = :username and m.age > :age", Member.class)
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    // 2. 쿼리 메소드 : 메소드 이름으로 JPA NamedQuery 호출
    //    메소드 이름으로 JPA NamedQuery 호출 : 실무에서 거의 안 씀.
    //    이 기능도 실무에서 거의 사용하지 않는다.
    public List<Member> findByUsername(String username) {
            // createNamedQuery 사용에 주의할 것...
            return em.createNamedQuery("Member.findByUsername", Member.class)
                    .setParameter("username", username)
                    .getResultList();
    }

    // 순수 JPA페이징 하기.  =============================================================================================
    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member m   " +
                                     " where m.age = :age      " +
                                     " order by m.username desc" , Member.class)
                .setParameter("age", age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
    // 전체 건수 가져오기
    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }
    // 순수 JPA페이징 하기.  =============================================================================================


    // 순수 JPA벌크 수정하기. =============================================================================================
    public int bulkAgePlus(int age) {
        return em.createQuery(
                "update Member m            " +
                        "  set m.age = m.age * 100 " +
                        "where m.age <= :age       " )
                .setParameter("age", age)
                .executeUpdate();
    }
    // 순수 JPA벌크 수정하기. =============================================================================================

}
