package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity @Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // Default 생성자 생략 옵션.
@ToString(of={"id", "username","age"})              // Team과 같은 연관관계 필드는 빼고 출력
@NamedQuery(                                        // 메소드 이름으로 JPA NamedQuery 호출 : 실무에서 거의 안 씀.
        name="Member.findByUsername",
        query="select m from Member m where m.username = :username")
//public class Member extends JpaBaseEntity{    /* 순수 JPA 방식 Auditing */
public class Member extends BaseEntity {        /* 스프링 Data JPA 방식 Auditing */

    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)  // xToOne은 fetch 옵션을 반드시 지연로딩(LAZY)으로 세팅해야 한다.
    @JoinColumn(name="team_id")         // FK를 가지고 있는 쪽이 연관관계 주인이다.
    private Team team;

    // JPA는 기본적으로 Default 생성자를 반드시 생성해야 하는대,
    // @NoArgsConstructor(access = AccessLevel.PROTECTED) 이 어노테이션 사용시 생략함.
//    protected Member() {
//    }

    public Member(String username) {
        this.username = username;
    }

    // 테스트용 생성자
    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }


    /**
     * <연관관계 메소드>
     *    - 연관관계 메소드를 반드시 생성해 줘야 한다.
     *    - 서로 연관관계를 세팅하는 옵션
     *    - Member는 Team을 변경할 수 있어야 한다.
     *    - 연관관계 주인쪽에 세팅한다.
     * */
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }


}
