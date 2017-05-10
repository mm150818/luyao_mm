package top.toybus.luyao.common;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import top.toybus.luyao.api.entity.User;
import top.toybus.luyao.api.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("dev")
public class CommonTests {

	@Autowired
	private UserRepository userRepository;

	@Test
	public void test1() {
		Pageable pageable = new PageRequest(0, 1);
		Page<User> page = userRepository.findAll(pageable);
		System.out.println(page.getContent());
	}

	@Test
	public void test2() throws Exception {
		User user = userRepository.findOne(1L);
		System.out.println(Jackson2ObjectMapperBuilder.json().build().writeValueAsString(user));
	}

	public static void main(String[] args) {
		System.out.println(LocalDateTime.now());
	}

}
