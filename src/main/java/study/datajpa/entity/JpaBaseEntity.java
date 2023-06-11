package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

/**
 *  <Auditing>
 * 엔티티를 생성, 변경할 때 변경한 사람과 시간을 추적하고 싶으면?
 *    - 등록일
 *    - 등록자
 *    - 수정일
 *    - 수정자
 *
 * */

/**
 *  <순수 JPA 사용 방법>
 * */
@MappedSuperclass // 속성들을 자식 클래스에 내려서 사용할 있게 하는 옵션.
@Getter
public class JpaBaseEntity {
    
    @Column(updatable = false)                   // 등록일 사후에 변경되지 못하도록 처리
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    @Column(updatable = false)                   // 등록일 사후에 변경되지 못하도록 처리
    private String createdById = "IN_ADMIN";         // 등록자ID
    private String updatedById ="UP_ADMIN";         // 수정자ID

    @PrePersist                                   // 등록 전 필수 저장 로그 기록
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdById = "IN_ADMIN";                 // 등록자ID
        createdDate = now;
        updatedById ="IN_ADMIN";                  // 수정자ID
        updatedDate = now;
    }

    @PreUpdate                                    // 수정 전 필수 저장 로그 기록
    public void preUpdate() {
        LocalDateTime now = LocalDateTime.now();
        updatedById ="UP_ADMIN";                  // 수정자ID
        updatedDate = now;
    }
}
