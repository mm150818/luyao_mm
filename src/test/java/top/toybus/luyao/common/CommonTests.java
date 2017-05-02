package top.toybus.luyao.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import top.toybus.luyao.api.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CommonTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void test1() {
	System.out.println("sss" + userRepository);
    }

}
