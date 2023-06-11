package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

// 1. MemberRepository 로 해결이 안될 때
// 2. 복잡한 쿼리를 생성해야 할 때
// 3. 구현 클래스 생성,
//     - 작성 규칙이 중요하다.
//     - MemberRepository + Impl
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    // 순수한 JPA기능을 직접 쓰고자 할 때...
    @PersistenceContext
    private final EntityManager em;

    // @PersistenceContext 사용 시 생성자 생략 가능
//    public MemberRepositoryImpl(EntityManager em) {
//        this.em = em;
//    }

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }

}
