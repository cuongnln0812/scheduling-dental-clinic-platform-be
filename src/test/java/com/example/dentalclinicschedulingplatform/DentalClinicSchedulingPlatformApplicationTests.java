package com.example.dentalclinicschedulingplatform;

import com.example.dentalclinicschedulingplatform.service.IDentistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class DentalClinicSchedulingPlatformApplicationTests {

	@Autowired
	private IDentistService dentistService;

	@Test
	public void contextLoads() {
		assertThat(dentistService).isNotNull();
	}
}
