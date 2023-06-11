package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@EnableJpaAuditing                               // 스프링 데이터 JPA Auditing 사용 시 :  스프링 부트 설정 클래스에 적용해야함
//@EnableJpaAuditing(modifyOnCreate = false)     // @EnableJpaAuditing(modifyOnCreate = false) 시 UpdateID는 null로 저장된다.
@SpringBootApplication
//@EnableJpaRepositories(basePackages = "study.datajpa.repository")  // 원칙은 이렇게 작성해 주어야 하지만, 스프링부트에서 위치를 자동으로 세팅해 준다.
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	// 스프링 데이터 JPA Auditing 사용 시 ==============================================
	private String createdByID = "IN_ADMIN";

	@Bean
	public AuditorAware<String> auditorProvider() {
//		return new AuditorAware<String>() {
//			@Override
//			public Optional<String> getCurrentAuditor() {
//				return Optional.of(UUID.randomUUID().toString());
//			}
//		};
		// 람다 형태의 1줄로 바꾸기..
		// 세션정보 가져와서 User_ID를 세팅해 주면된다.
		//return () -> Optional.of(UUID.randomUUID().toString());
		return () -> Optional.of(createdByID);
	}
	// 스프링 데이터 JPA Auditing 사용 시 ==============================================

}
