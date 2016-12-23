package com.pingan.stock.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import jdk.nashorn.api.scripting.JSObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

@Controller

public class HelloController {

    //sandbox config
    String aliPayGateway = "https://openapi.alipaydev.com/gateway.do";
    String appId = "2016080100143996";
    String appPrivateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMpyPriMcDVKQO1cVXnmVpi5/JiX25XZJW61oBl+/cx6yVFB3/h5KYju6oz0CsmMM5KJ4aOxRjEgQ1wf6ku80mAlghjmTHwldCUNgn7Pwwk4prk4rWrfERdgFuG+oFVZtz06K6IEZgp1U4OVt8/SPCJwbHqZ+tcYmdG5f2OX6nVXAgMBAAECgYEAj30oLYAE7BCEMni2cN8s07VHbxR7ZBtz56M+JbQXyX3iAQES8TdXlCHbByrWFoKT7zJhRreYi6tVcw9/7kyYQqB11bsYHvdL77hzXtv5kqXzb0YF+xGExBoMVMLyWo8rXMCBodqpWXO2hcrd52MugJO02wwQFa7ltN4fs1VqaukCQQDtO1zoDoi+dmm/8ouO6wP6VFRvbeXSoqEpFFJYAMaSpHExj3vnqf1RpIFlXfr9VEmnef98t9lFNEZpPRMyKq59AkEA2nZfOUi8YFheVSrQEWgcyjNkXWBWm8t54ugt3jARtwfior6f9oCbaQaF+y5Ax1wZ3vIJLprKpLygsTx4PLbXYwJAQII88ElLe+c9OFnfAzz69u5Ji4dp0E4y3rXM8ms7lBKtSRnISqWZ4cKHASZ+Irbx1F3DUIm5xizB3Moj1yduDQJASV82TiwCsK0dSaduaFo6SSMHk2D21a56Sl5GgXhNitaIJLa3TPgWuihpuVnogcyKF9ncgZ2Vb8y2f7BahbG3FwJBALc1ZoAs7WxfxdT702p9EgpsjIh7HDxe5wKB0MwE2lljPJS7MDcyn0orIewBamJtzYdFHATqSvRz1NfdPJ5fOYo=";
    String aliPayPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDIgHnOn7LLILlKETd6BFRJ0GqgS2Y3mn1wMQmyh9zEyWlz5p1zrahRahbXAfCfSqshSNfqOmAQzSHRVjCqjsAw1jyqrXaPdKBmr90DIpIxmIyKXv4GGAkPyJ/6FTFY99uhpiq0qadD/uSzQsefWo0aTvP/65zi3eof7TcZ32oWpwIDAQAB";
    String charSet = "utf-8";


    AlipayClient alipayClient = new DefaultAlipayClient(aliPayGateway, appId, appPrivateKey, "json", charSet, aliPayPublicKey);
    String lastOutTradeNo;
    String tradeNoPrefix = "20161222";
    int randomNo = new Random().nextInt(10000000)+100000000;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String printWelcome(ModelMap model) {
        model.addAttribute("message", "Alipay Demo");
        return "alipayDemo";
    }

    @RequestMapping(value = "/payTest", method = RequestMethod.POST)
    public String payTest(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException, AlipayApiException {
        String outTradeNo = tradeNoPrefix + randomNo;
        randomNo++;
        lastOutTradeNo = outTradeNo;
        AlipayTradeWapPayRequest tradeRequest = new AlipayTradeWapPayRequest();//创建API对应的request
        tradeRequest.setReturnUrl("http://localhost:8080/AliPayDemo/payReturn");
        tradeRequest.setNotifyUrl("http://localhost:8080/AliPayDemo/payReturnNotice");//在公共参数中设置回跳和通知地址
        tradeRequest.setBizContent("{" +
                "    \"out_trade_no\":\""+outTradeNo+"\"," +
                "    \"total_amount\":1," +
                "    \"subject\":\"Iphone6 16G\"," +
                "    \"seller_id\":\"2088102169504675\"," +
                "    \"product_code\":\"QUICK_WAP_PAY\"" +
                "  }");//填充业务参数
        String form = alipayClient.pageExecute(tradeRequest).getBody(); //调用SDK生成表单
        httpResponse.setContentType("text/html;charset=" + "UTF-8");
        httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        return null;
    }

    @RequestMapping(value = "/payReturn", method = RequestMethod.GET)
    public String handlePayReturn(HttpServletRequest httpRequest, HttpServletResponse httpResponse, ModelMap model) throws AlipayApiException {
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = httpRequest.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        String outTradeNo = params.get("out_trade_no");

        boolean signVerified = AlipaySignature.rsaCheckV1(params, aliPayPublicKey, charSet);

        if(signVerified){
            try {
                httpResponse.getWriter().println("success");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                httpResponse.getWriter().println("fail");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        model.addAttribute("outTradeNo", outTradeNo);
        return "paymentResult";
    }

    @RequestMapping(value = "/queryTrade", method = RequestMethod.POST)
    public String queryTrade(HttpServletRequest httpRequest, HttpServletResponse httpResponse, ModelMap model) throws IOException, AlipayApiException {
        String outTradeNo = httpRequest.getParameter("outTradeNo");
        AlipayTradeQueryRequest queryRequest = new AlipayTradeQueryRequest();
        String bizContent = "{\"out_trade_no\":\""+outTradeNo+"\"}";
        queryRequest.setBizContent(bizContent);
        AlipayTradeQueryResponse queryResponse = alipayClient.execute(queryRequest);
        String tradeStatus = "";
        String outTradeNoFromQuery = "";
        String amount = "";
        if (queryResponse != null) {
            tradeStatus = queryResponse.getTradeStatus();
            outTradeNoFromQuery = queryResponse.getOutTradeNo();
            amount = queryResponse.getTotalAmount();
        }
        model.addAttribute("tradeStatus", tradeStatus);
        model.addAttribute("outTradeNo", outTradeNoFromQuery);
        model.addAttribute("amount", amount);
        return "tradeInfo";
    }

    @RequestMapping(value = "/refundTrade", method = RequestMethod.POST)
    public String refundTrade(HttpServletRequest httpRequest, HttpServletResponse httpResponse, ModelMap model) throws IOException, AlipayApiException {
        String outTradeNo = httpRequest.getParameter("outTradeNo");
        String amount = httpRequest.getParameter("amount");
        String outRequestNo = String.valueOf(randomNo);
        AlipayTradeRefundRequest refundRequest = new AlipayTradeRefundRequest();
        String bizContent = "{\"out_trade_no\":\""+outTradeNo+"\", \"out_request_no\":\""+outRequestNo+"\",\"refund_amount\":\""+amount+"\"}";
        refundRequest.setBizContent(bizContent);
        AlipayTradeRefundResponse refundResponse = alipayClient.execute(refundRequest);

        return "";
    }


    @RequestMapping(value = "/queryBill", method = RequestMethod.POST)
    public String queryBill(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException, AlipayApiException {
        AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
        String bizContent = "{\"bill_type\":\"trade\", \"bill_date\":\"2016-12-21\"}";
        request.setBizContent(bizContent);
        AlipayDataDataserviceBillDownloadurlQueryResponse response = alipayClient.execute(request);
        String billUrl = response.getBillDownloadUrl();

        String filePath = "d:\\wls";
        URL url = null;
        HttpURLConnection httpUrlConnection = null;
        InputStream fis = null;
        FileOutputStream fos = null;
        try {
            url = new URL(billUrl);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setConnectTimeout(5 * 1000);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.setRequestProperty("Charsert", "UTF-8");
            httpUrlConnection.connect();
            fis = httpUrlConnection.getInputStream();
            byte[] temp = new byte[1024];
            int b;
            fos = new FileOutputStream(new File(filePath));
            while ((b = fis.read(temp)) != -1) {
                fos.write(temp, 0, b);
                fos.flush();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(fis!=null) fis.close();
                if(fos!=null) fos.close();
                if(httpUrlConnection!=null) httpUrlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}