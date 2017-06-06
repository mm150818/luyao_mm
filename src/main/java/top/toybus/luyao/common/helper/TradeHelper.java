package top.toybus.luyao.common.helper;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import top.toybus.luyao.common.properties.TradeProperties;
import top.toybus.luyao.common.util.IpUtils;

/**
 * 支付助手
 * 
 * @author sunxg
 */
@Log4j
@Component
public class TradeHelper {
    @Getter
    @Autowired
    public TradeProperties tradeProps;

    private AlipayClient alipayClient;

    @PostConstruct
    private void init() {
        // System.out.println(tradeProps);
        // 实例化客户端
        alipayClient = new DefaultAlipayClient(tradeProps.getALI_URL(), tradeProps.getALI_APP_ID(),
                tradeProps.getALI_APP_PRIVATE_KEY(), tradeProps.getALI_FORMAT(), tradeProps.getALI_CHARSET(),
                tradeProps.getALI_ALIPAY_PUBLIC_KEY(), tradeProps.getALI_SIGN_TYPE());
    }

    /**
     * 获得支付订单
     */
    public Map<String, Object> aliAppPay(Long orderNo, String body, Long totalAmount) {
        // 实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        // SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setSubject(body);
        model.setOutTradeNo(orderNo.toString());
        // model.setTimeoutExpress("30m");
        model.setTotalAmount(String.format("%.2f", totalAmount.longValue() / 100.0));
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl(tradeProps.getALI_NOTIFY_URL());

        Map<String, Object> resultMap = new HashMap<>();
        try {
            // 这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            if (response.isSuccess()) {
//                System.out.println(response.getBody());
                // 就是orderString 可以直接给客户端请求，无需再做处理。
                String orderStr = response.getBody();
                resultMap.put("payOrder", orderStr);
            }
        } catch (AlipayApiException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return resultMap;
    }

    /**
     * 支付宝交易查询
     */
    public String AliQuery(String outTradeNo) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(outTradeNo);
        request.setBizModel(model);
        String resultJson = null;
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                resultJson = response.getBody();
            }
        } catch (AlipayApiException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return resultJson;
    }

    public boolean verifyAliSign(Map<String, String[]> paramsMap) {
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        for (Iterator<String> iter = paramsMap.keySet().iterator(); iter.hasNext();) {
            String name = iter.next();
            String[] values = paramsMap.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        // 切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
        try {
            return AlipaySignature.rsaCheckV2(params, tradeProps.getALI_ALIPAY_PUBLIC_KEY(),
                    tradeProps.getALI_CHARSET(), tradeProps.getALI_SIGN_TYPE());
        } catch (AlipayApiException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获得微信签名
     */
    private String getSign(Map<String, Object> paramsMap) {
        List<String> list = new ArrayList<String>();
        for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
            if (entry.getValue() != "") {
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
        }
        sb.append("key=").append(tradeProps.getWX_KEY());
        String result = sb.toString();
        result = DigestUtils.md5Hex(result).toUpperCase();
        return result;
    }

    /**
     * 发送Https POST请求
     */
    private Map<String, Object> doHttpsPost(String url, Map<String, Object> paramsMap) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        FileInputStream instream = null;
        CloseableHttpResponse httpResponse = null;
        Map<String, Object> resMap = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        params.put("appid", tradeProps.getWX_APPID());
        params.put("mch_id", tradeProps.getWX_MCH_ID());
        params.put("nonce_str", RandomStringUtils.randomAlphanumeric(32));
        params.putAll(paramsMap);
        params.put("sign", getSign(params));
        try {
            if (tradeProps.getWX_REFUND_API().equals(url)) {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                instream = new FileInputStream(new File(tradeProps.getWX_CERT_LOCAL_PATH()));// 加载本地的证书进行https加密传输
                keyStore.load(instream, tradeProps.getWX_CERT_PASSWORD().toCharArray());// 设置证书密码

                // Trust own CA and all self-signed certs
                SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                        .loadKeyMaterial(keyStore, tradeProps.getWX_CERT_PASSWORD().toCharArray()).build();
                // Allow TLSv1 protocol only
                SSLConnectionSocketFactory.getSocketFactory();
                SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
                        new String[] { "TLSv1" }, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());

                httpClient = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
            } else {
                httpClient = HttpClients.createDefault();
            }

            ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.xml().build();
            String postDataXML = objectMapper.writerFor(Map.class).withRootName("xml").writeValueAsString(paramsMap);
            StringEntity postEntity = new StringEntity(postDataXML, "UTF-8");
//            httpPost.addHeader("Content-Type", "text/xml");
            httpPost.setEntity(postEntity);

            httpResponse = httpClient.execute(httpPost);
            HttpEntity entity = httpResponse.getEntity();
            String result = EntityUtils.toString(entity, "UTF-8");
            resMap = objectMapper.readerFor(Map.class).readValue(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
                if (instream != null) {
                    instream.close();
                }
                httpPost.abort();
                httpClient.close();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        return resMap;
    }

    /**
     * 微信统一下单
     */
    public Map<String, Object> wxAppPay(Long orderNo, String body, Long totalAmount) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("out_trade_no", orderNo);
        paramsMap.put("body", body);
        paramsMap.put("total_fee", totalAmount);
        paramsMap.put("spbill_create_ip", IpUtils
                .getIpAddr(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()));
        paramsMap.put("notify_url", tradeProps.getWX_NOTIFY_URL());
        paramsMap.put("trade_type", "APP");

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("payOrder", this.doHttpsPost(tradeProps.getWX_UNIFIEDORDER_API(), paramsMap));
        return resultMap;
    }

    /**
     * 微信查询订单
     */
    public Map<String, Object> wxOrderQuery(String outTradeNo) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("out_trade_no", outTradeNo);
        Map<String, Object> resultMap = this.doHttpsPost(tradeProps.getWX_ORDERQUERY_API(), paramsMap);
        return resultMap;
    }

    /**
     * 获得微信请求参数
     */
    public Map<String, Object> getWxReqParamsMap() {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.xml().build();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        Map<String, Object> paramsMap = new HashMap<>();
        try {
            paramsMap = objectMapper.readerFor(Map.class).readValue(request.getReader());
            return paramsMap;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return paramsMap;
    }

    /**
     * 验证微信请求签名
     */
    public boolean verifyWxSign(Map<String, Object> paramsMap) {
        Object signObj = paramsMap.get("sign");
        if (signObj == null || StringUtils.isBlank(signObj.toString())) {
            return false;
        }
        paramsMap.put("sign", "");
        String sign = this.getSign(paramsMap);
        if (!sign.equals(signObj.toString())) {
            return false;
        }
        return true;
    }

    /**
     * 统一下单
     */
    public Map<String, Object> unifiedOrder(Integer way, Long orderNo, String body, Long totalAmount) {
        Map<String, Object> resultMap = null;
        if (way == 1) {
            resultMap = this.aliAppPay(orderNo, body, totalAmount);
        } else if (way == 2) {
            resultMap = this.wxAppPay(orderNo, body, totalAmount);
        }
        return resultMap;
    }

    public static void main(String[] args) throws Exception {
//        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.xml().build();
//        Map<String, String> map = new HashMap<>();
//        map.put("name", "[{\"aaa\":111}]");
//        System.out.println(objectMapper.writerFor(Map.class).withRootName("xml").writeValueAsString(map));
//
//        String xml = "<map><name>sunxg</name><value></value></map>";
//        System.out.println((Object) objectMapper.readerFor(Map.class).readValue(xml));

//        System.out.println(String.format("%.2f", 133 / 100.0));
    }

}
