import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.axis2.AxisFault;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import com.chinamobile.openmas.client.Sms;
public class HelloWorld {
	//发件人地址
	private static String senderAddress = "bfwzqwer@163.com";
    //收件人地址
    private static String recipientAddress = "417299862@qq.com";
    //发件人账户名
    private static String senderAccount = "bfwzqwer@163.com";
    //发件人账户密码
    private static String senderPassword = "159753qwer";
    private static String Url = "http://gbk.api.smschinese.cn";
    
    private static String SMS_APPLICATION_URL = "http://111.1.3.184:9080/OpenMasService?wsdl";
    private static String SMS_APPLICATION_EXTENDCODE = "3";
    private static String SMS_APPLICATION_ID = "OA";
    private static String SMS_APPLICATION_PASSWORD = "hxerpmas";
    
    public static void main(String args[]){    
        URL url;
        int responsecode;
        HttpURLConnection urlConnection;
        BufferedReader reader;
        String line;
        try{
        	String[] tels = {"13065831309"};
        	SendSms(tels,"hah");
            url=new URL("http://wx.zs.zj.chinamobile.com/app/process/goodsList.do?usnumber=29");
            while(true){
	            urlConnection = (HttpURLConnection)url.openConnection();
	            responsecode=urlConnection.getResponseCode();
	            if(responsecode==200){
	                reader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
	                String Regex = "\\'\\d{2}\\'\\,\\'\\d{3,4}\\'\\,\\d+";
	                Pattern p=Pattern.compile(Regex);
	                boolean flag = false;
                	while((line=reader.readLine())!=null){
                		Matcher matcher=p.matcher(line);
                		if(matcher.find()){
                			String str = matcher.group();
                			String[] strs = str.split(",");
                			if(strs!=null && strs.length>0){
                				System.out.println(str);
                				if(!"0".equals(strs[2])){
                					flag = true;
                				}
                			}
                		}
                	}
                	if(flag){
                		System.out.println("可以抢了！");
                		sendSMS("可以抢了！【dreamtale】");
                		sendMail("可以抢了！【dreamtale】");
                		break;
                	}
	            }
	            else{
	                System.out.println("获取不到网页的源码，服务器响应代码为"+responsecode);
	            }
	        }
        }
        catch(Exception e){
            System.out.println("获取不到网页的源码,出现异常："+e);
        }
    }
    
    public static void sendSMS(String message) throws Exception {
    	HttpClient client = new HttpClient();
    	PostMethod post = new PostMethod(Url); 
    	post.addRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=gbk");//在头文件中设置转码
    	NameValuePair[] data ={ new NameValuePair("Uid", "dreamtale"),new NameValuePair("Key", "c1f9f60e8708fc462d5b"),new NameValuePair("smsMob","13065831309"),new NameValuePair("smsText",message)};
    	post.setRequestBody(data);

    	client.executeMethod(post);
    	Header[] headers = post.getResponseHeaders();
    	int statusCode = post.getStatusCode();
    	System.out.println("statusCode:"+statusCode);
    	for(Header h : headers)
    	{
    	System.out.println(h.toString());
    	}
    	String result = new String(post.getResponseBodyAsString().getBytes("gbk")); 
    	System.out.println(result); //打印返回消息状态


    	post.releaseConnection();
    }
    
    /**
	 * 使用 OpenMas 实现发送短信
	 * 
	 * @param destinationAddress
	 * 								发送人地址
	 * @param message
	 * 							发送短信内容
	 * @return
	 */
	private static String SendSms(String[] destinationAddresses, String message) {
		// 短信 ID, OpenMAS 上的唯一标识
		String messageId = null; 
		try {
			System.out.println(" --------------- send message start --------------- ");
			Sms client = new Sms(SMS_APPLICATION_URL);
			messageId = client.SendMessage(destinationAddresses, message, 
					SMS_APPLICATION_EXTENDCODE, SMS_APPLICATION_ID, SMS_APPLICATION_PASSWORD);
			System.out.println(" --------- messageId：" + messageId + " ----------- ");
			System.out.println(" --------------- send message  end  --------------- ");
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println(e);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return messageId;
	}
    
    public static void sendMail(String message) throws Exception {
    	//1、连接邮件服务器的参数配置
        Properties props = new Properties();
        //设置用户的认证方式
        props.setProperty("mail.smtp.auth", "true");
        //设置传输协议
        props.setProperty("mail.transport.protocol", "smtp");
        //设置发件人的SMTP服务器地址
        props.setProperty("mail.smtp.host", "smtp.163.com");
        //2、创建定义整个应用程序所需的环境信息的 Session 对象
        Session session = Session.getInstance(props);
        //设置调试信息在控制台打印出来
        session.setDebug(true);
        //3、创建邮件的实例对象
        Message msg = getMimeMessage(session,message);
        //4、根据session对象获取邮件传输对象Transport
        Transport transport = session.getTransport();
        //设置发件人的账户名和密码
        transport.connect(senderAccount, senderPassword);
        //发送邮件，并发送到所有收件人地址，message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(msg,msg.getAllRecipients());
         
        //如果只想发送给指定的人，可以如下写法
        //transport.sendMessage(msg, new Address[]{new InternetAddress("xxx@qq.com")});
         
        //5、关闭邮件连接
        transport.close();
    }
    
    /**
     * 获取邮件内容
     * @param session
     * @return
     * @throws MessagingException
     * @throws AddressException
     */
    public static MimeMessage getMimeMessage(Session session,String message) throws Exception{
    	//创建一封邮件的实例对象
        MimeMessage msg = new MimeMessage(session);
        //设置发件人地址
        msg.setFrom(new InternetAddress(senderAddress));
        /**
         * 设置收件人地址（可以增加多个收件人、抄送、密送），即下面这一行代码书写多行
         * MimeMessage.RecipientType.TO:发送
         * MimeMessage.RecipientType.CC：抄送
         * MimeMessage.RecipientType.BCC：密送
         */
        msg.setRecipient(MimeMessage.RecipientType.TO,new InternetAddress(recipientAddress));
        //设置邮件主题
        msg.setSubject("邮件主题","UTF-8");
        //设置邮件正文
        msg.setContent(message, "text/html;charset=UTF-8");
        //设置邮件的发送时间,默认立即发送
        msg.setSentDate(new Date());
         
        return msg;
    }
}