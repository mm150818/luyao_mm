package top.toybus.luyao.sms;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import top.toybus.luyao.common.helper.SmsHelper;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("dev")
public class smsTests {
    @Autowired
    private SmsHelper smsHelper;

    @Test
    public void test1() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("name", "上海到马洲");
        paramMap.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("M月d日HH点mm分")));
        paramMap.put("address", "上海火车站");
//		System.out.println(smsHelper.smsProperties.getTplOrderOkForOwner()());
//		smsHelper.sendSms("13661561730", smsHelper.smsProperties.getTplOrderOk(), paramMap);
    }

    @Test
    public void test2() throws Exception {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("name", "上海到马洲");
//		paramMap.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("M月d日HH点mm分")));
//		paramMap.put("time", "ddd");
//		paramMap.put("address", "上海火车站");
        smsHelper.sendSms("13661561730", "SMS_67225187", paramMap);
    }
}
