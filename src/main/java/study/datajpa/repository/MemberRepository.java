package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;

// <확장기능>
//    - MemberRepositoryCustom 추가함으로써 상송받는다.
//    - 사용자 정의 리포지토리 구현
//    - 다양한 이유로 인터페이스의 메서드를 직접 구현하고 싶을 경우.
//       1). JPA 직접 사용( EntityManager )
//       2). 스프링 JDBC Template 사용
//       3). MyBatis 사용
//       4). 데이터베이스 커넥션 직접 사용 등등...
//       5). Querydsl 사용
public interface MemberRepository extends JpaRepository<Member, Long>
        , MemberRepositoryCustom
        , JpaSpecificationExecutor<Member>   //  Specifications (명세) 사용 시 추가함.
{

    // 구현제(쿼리)를 만들지 않고, 인터페이스만 만들어도 동작이 된다.
    // Username 기본 '=' 조건, Age  '>'  GreaterThan
    // 주로 조건 2~3개까지만 사용한다.
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3HelloBy();

    // 이 기능도 실무에서 거의 사용하지 않는다.
    // 3. 쿼리 메소드 : @Query 어노테이션을 사용해서 리파지토리 인터페이스에 쿼리 직접 정의
//    @Query(name="Member.findByUsername")  // @Query 어노테이션은 있어도 되고, 없어도 된다.
    List<Member> findByUsername(@Param("username") String username);


    // 4. 쿼리 메소드 : @Query 어노테이션을 사용해서 리파지토리 인터페이스에 쿼리 직접 정의
    //    강사가 가장 강력하게 추천하는 방식
    //    - 장점1 : 쿼리 오류 시 어플리케이션 로딩 시 오류를 띄워 줌...
    //    - 장점2 : 실무에서 가장 많이 사용함.
    //    - 장점3 : 복잡한 쿼리를 만들기에 유용 함.
    //    단, 동적쿼리는 Query DSL 을 사용해야 함.
    @Query("select m from Member m " +
            " where m.username = :username " +
            " and m.age > :age"
    )
    List<Member> findUser(@Param("username") String username, @Param("age") int age);


    // 5. @Query, 값, DTO 조회하기
    //   - 단순히 값 하나를 조회
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    // 6. @Query, 값, DTO 조회하기
    //   - DTO로 직접 조회
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) " +
            "from Member m join m.team t")
    List<MemberDto> findMemberDto();

    // 7. 파라미터 바인딩 :
    //    - 이름기반 파라미터 바인딩 테스트. (위치 기반은 절대 쓰지 말 것...)
    @Query( " select m from Member m " +
            "  where m.username like %:username% " +
            "    and m.age > :age"
    )
    List<Member> findMembers(@Param("username") String username, @Param("age") int age);

    // 8. 파라미터 바인딩 :
    //    - Collection 타입으로 in절 지원
    //    - 실무에서 많이 씀
    @Query( " select m from Member m " +
            "  where m.username in :names "
    )
    //List<Member> findByNamesIn(@Param("names") List<String> names);
    List<Member> findByNamesIn(@Param("names") Collection<String> names); // List보다는 Collection으로 사용을 추천함.


    // 쿼리가 복잡할 경우 Count 쿼리를 분리하면 성능이 보장된다.
    //@Query(value = "select m from Member m left join m.team t",
    //        countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);


    // 스프링 Data JPA 벌크 수정하기. =====================================================================================
    // 벌크 작업을 진행하려면 @Modifying 어노테이션을 이용해야 한다.
    // 벌크 작업이 완료 후 자동 클리어(clearAutomatically) 해 주는 옵션을 넣어 주어야 한다.
    // clearAutomatically = true 옵션이 넣지 않으면, 업데이트 후 같은 트랜젝션에서 재조회 시 이전 값이 조회됨을 주의해야 한다.
    @Modifying(clearAutomatically = true)
    @Query("update Member m           " +
            "  set m.age = m.age * 10 " +
            "where m.age >= :age      " )
    int bulkAgeUpdate(@Param("age") int age);
    // 스프링 Data JPA 벌크 수정하기. =====================================================================================

    // 지연로딩 패치조인
    //    - 연관된 엔티티를 한번에 조회하려면 페치 조인이 필요하다.
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();


    // 엔티티 그래프 (EntityGraph)  ======================================================================================
    // <EntityGraph 정리>
    //   - 사실상 페치 조인(FETCH JOIN)을 간편하게 사용하는 기능
    //   - LEFT OUTER JOIN 사용
    //   - 간단할 때 앤티티그래프가 최적의 활용. - 복잡할 때는 JPQL 패치조인을 사용한다.
    @Override
    @EntityGraph(attributePaths = {"team"})  // 지연로딩 하지 말고, 강제로 패치조인하도록 유도하기.
    List<Member> findAll();


    // JPQL + 엔티티 그래프
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();


    // 메서드 이름으로 쿼리에서 특히 편리하다.
    //@EntityGraph(attributePaths = {"team"})
    //List<Member> findByUsername(String username);


    // NamedEntityGraph
    //@EntityGraph("Member.all")
    //@Query("select m from Member m")
    //List<Member> findMemberEntityGraph();

    // 엔티티 그래프 (EntityGraph)  ======================================================================================



    // JPA Hint ========================================================================================================
    // JPA 쿼리 힌트(SQL 힌트가 아니라 JPA 구현체에게 제공하는 힌트)
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value="true"))
    Member findReadOnlyByUsername(String username);

    @QueryHints(value = { @QueryHint(name = "org.hibernate.readOnly",
            value = "true")},
            forCounting = true)
    Page<Member> findByUsername(String name, Pageable pageable);
    // JPA Hint ========================================================================================================



    // JPA Lock ========================================================================================================
    //    - JPA Lock 사용으로 조회된 데이터 보호하기
    //    - select ... for update 가 자동으로 쿼리 수행된다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Member findLockByUsername(String username);

    // JPA Lock ========================================================================================================


    List<UsernameOnly> findProjectionsByUsername(@Param("username") String name);

    List<UsernameOnlyDto> findProjectionDtoByUsername(@Param("username") String username);

    // <동적 Projections>
    //     - 동적 프로젝션
    //     - 제너릭 타입을 전송해서 사용 가능
    <T> List <T> findProjectionDtoByUsername(@Param("username") String username, Class<T> type);


    /**
     * <네이티브 쿼리 사용하기>
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
     * */
    @Query(value="select * from member where username like ? and age between ? and ? " , nativeQuery = true)
    List<Member> findByNativeQuery(String username, int ageFrom, int ageTo);



    /**
     * <Projections 활용>
     *     - 예) 스프링 데이터 JPA 네이티브 쿼리 + 인터페이스 기반 Projections 활용
     *     - 네이티브 쿼리 활용
     *     - 페이징 활용
     *
     * */
    @Query(value="select m.member_id as id         " +
                 "     , m.username  as username   " +
                 "     , t.name      as teamName   " +
                 "  from member m left join team t " +
                 " where username like ?           " +
                 "   and age between ? and ?       "
                 , countQuery = "select  count(*)  " +
                 "  from Member                    " +
                 " where username like ?           " +
                 "   and age between ? and ?       "
                 , nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(String username, int ageFrom, int ageTo, String username2, int ageFrom2, int ageTo2, Pageable pageable);




}
