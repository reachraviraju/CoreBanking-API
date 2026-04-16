package com.corebanking.CoreBanking_API;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Keep context test independent from local MySQL by using in-memory H2 settings.
@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:corebanking;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.jpa.open-in-view=false",
		"springdoc.api-docs.enabled=false",
		"springdoc.swagger-ui.enabled=false",
		"security.jwt.secret=test-only-change-me-min-32-chars-for-hs256-key!!",
		"security.jwt.expiration-ms=86400000"
})
class CoreBankingApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
