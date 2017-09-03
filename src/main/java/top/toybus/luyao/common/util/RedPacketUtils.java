package top.toybus.luyao.common.util;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional
public class RedPacketUtils {
   
	private static final double MINMONEY=0.1; //每人每天最小金额
	
	private static final double MAXMONEY=0.7;  //每人每天最大金额
	
	private static final double SUMMONEY=70;   //每天最大金额
	
	private static final int COUNT=20;         //每天红包总数量
	
    public String random() {                     //产生随机值红包
		
    	double temp_money=70;
    	String str_money=null;
        
    	if(temp_money>0) {
    		
    		double red_packet=Math.random()*0.7;
    		
        	System.out.println(red_packet);
    		
        	temp_money=SUMMONEY-red_packet;	
        	
        	str_money=String.valueOf(red_packet);
    	
    	}else {
    		return null;
    	}
	    	
    	return str_money;
			       
	    } 	
}
