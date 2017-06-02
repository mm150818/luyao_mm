package top.toybus.luyao.common;

import java.math.BigDecimal;

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

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import top.toybus.luyao.api.entity.User;
import top.toybus.luyao.api.entity.Vehicle;
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

    public static void main(String[] args) throws Exception {
//        System.out.println(LocalDateTime.now());
//        LocalDateTime now = LocalDateTime.now();
//        System.out.println(Duration.between(now, now.plusSeconds(61)).get(ChronoUnit.MINUTES));

        User user = new User();
        user.setToken("token");
        user.setBalance(new BigDecimal("1.21342424"));
        user.setMobile("13661561730");
        Vehicle vehicle = new Vehicle();
        vehicle.setModel("model");
        vehicle.setNo("no");
        user.setVehicle(vehicle);

        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.setDefaultFilter(SimpleBeanPropertyFilter.serializeAllExcept("balance"));
//        filterProvider.addFilter("userFilter", SimpleBeanPropertyFilter.filterOutAllExcept("mobile", "headImg", "img"));
        filterProvider
                .setDefaultFilter(SimpleBeanPropertyFilter.SerializeExceptFilter.serializeAllExcept("vehicle.img"));
        ObjectWriter objectWriter = Jackson2ObjectMapperBuilder.json().build()
                .setSerializationInclusion(Include.NON_NULL).setFilterProvider(filterProvider)
                .writerWithDefaultPrettyPrinter();
        System.out.println(objectWriter.writeValueAsString(user));

    }

}
