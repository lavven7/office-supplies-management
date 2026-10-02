package com.lavven777.officesupplies;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// Spring Boot 환경 전체를 실행해라.
@SpringBootTest
// 실행할 때 테스트용 설정을 사용해라.
@ActiveProfiles("test")
class OfficeSuppliesManagementApplicationTests {

	@Test
	void contextLoads() {
	}
}