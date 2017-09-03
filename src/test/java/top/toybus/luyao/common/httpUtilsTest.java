package top.toybus.luyao.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class httpUtilsTest {
	public static void main(String args[]){
		 String urlNameString="http://localhost:6080/api/message/list";

		 BufferedReader in = null;
		 String result="";
	        
		 try {
	            URL realUrl = new URL(urlNameString);
	            // 打开和URL之间的连接
	            URLConnection connection = realUrl.openConnection();
	            // 设置通用的请求属性
	            connection.setRequestProperty("accept", "*/*");
	            connection.setRequestProperty("connection", "Keep-Alive");
	            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	            // 建立实际的连接
	            connection.connect();
	           /* // 获取所有响应头字段
	            Map<String, List<String>> map = connection.getHeaderFields();
	            // 遍历所有的响应头字段
	            for (String key : map.keySet()) {
	                System.out.println(key + "--->" + map.get(key));
	            }*/
	            URL url =new URL(urlNameString);
	            InputStream is=url.openStream();
	            
	            //由于is是字节，要把它转换为String类型，否则遇到中文会出现乱码
	            BufferedReader reader=new BufferedReader(new InputStreamReader(is));
	            StringBuffer sb=new StringBuffer();
	            String line=null;
	            while((line=reader.readLine())!=null){
	                sb.append(line+"\n");
	            }
	            System.out.println(sb);

	        } catch (Exception e) {
	            System.out.println("发送GET请求出现异常！" + e);
	            e.printStackTrace();
	        }
	        // 使用finally块来关闭输入流
	        finally {
	            try {
	                if (in != null) {
	                    in.close();
	                }
	            } catch (Exception e2) {
	                e2.printStackTrace();
	            }
	    }
	}

}
