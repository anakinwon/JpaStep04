package study.datajpa.repository;

/**
 * <중첩 구조 처리>
 *
 * */
public interface NestedClosedProjections {
    String getUsername();
    TeamInfo getTeam();

    // 내부에 중첩으로 팀이름 가져오기.
    interface TeamInfo {
        String getName();
    }

}
