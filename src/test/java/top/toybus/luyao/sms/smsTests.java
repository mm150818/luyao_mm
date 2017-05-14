package top.toybus.luyao.sms;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.Message;

import top.toybus.luyao.common.properties.SmsProperties;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("dev")
public class smsTests {
	@Autowired
	private SmsProperties smsProperties;

	@Test
	public void test1() {
		CloudAccount account = new CloudAccount(smsProperties.getAccessKeyId(), smsProperties.getAccessKeySecret(),
				smsProperties.getEndpoint());
		// 这个client仅初始化一次
		MNSClient client = account.getMNSClient();
		// 循环发送10条消息
		try {
			// TestQueue是你的测试队列，请提前创建
			CloudQueue queue = client.getQueueRef("luyao");
			Message message = new Message();
			message.setMessageBody("I am test message");
			message.setPriority(8);
			Message putMsg = queue.putMessage(message);
			System.out.println("Send message id is: " + putMsg.getMessageId());
		} catch (ClientException ce) {
			System.out.println("Something wrong with the network connection between client and MNS service."
					+ "Please check your network and DNS availablity.");
			ce.printStackTrace();
		} catch (ServiceException se) {
			se.printStackTrace();
//			logger.error("MNS exception requestId:" + se.getRequestId(), se);
			if (se.getErrorCode() != null) {
				if (se.getErrorCode().equals("QueueNotExist")) {
					System.out.println("Queue is not exist.Please create before use");
				} else if (se.getErrorCode().equals("TimeExpired")) {
					System.out.println("The request is time expired. Please check your local machine timeclock");
				}
				/*
				you can get more MNS service error code from following link:
				https://help.aliyun.com/document_detail/mns/api_reference/error_code/error_code.html
				*/
			}
		} catch (Exception e) {
			System.out.println("Unknown exception happened!");
			e.printStackTrace();
		}
		client.close();
	}

	@Test
	public void test2() throws Exception {
	}
}
