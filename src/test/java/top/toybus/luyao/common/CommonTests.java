package top.toybus.luyao.common;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import top.toybus.luyao.api.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CommonTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void test1() {
	Pageable pageable = new PageRequest(0, 1);
    }

}
