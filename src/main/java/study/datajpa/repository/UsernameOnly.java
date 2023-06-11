package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;


/**
 * <Projections>
 *     - 엔티티 대신에 DTO를 편리하게 조회할 때 사용
 *     - 전체 엔티티가 아니라 만약 회원 이름만 딱 조회하고 싶으면?
 *
 *
 * */
public interface UsernameOnly {
    // 조회할 엔티티의 필드를 getter 형식으로 지정하면 해당 필드만 선택해서 조회(Projection)
    // Spring Data JPA 가 자동으로 만들어서 Return 해 줌.
    @Value("#{target.username + ' ' + target.age}")     // Open Projection 테스트하기 - 모두 다 가져오기.
    String getUsername();
}
