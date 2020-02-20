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
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.axis2.AxisFault;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import com.chinamobile.openmas.client.Sms;
public class HelloWorld {
	private static String senderAddress = "bfwzqwer@163.com";
    private static String recipientAddress = "417299862@qq.com";
    private static String senderAccount = "bfwzqwer@163.com";
    private static String senderPassword = "159753qwer";
    private static String Url = "http://gbk.api.smschinese.cn";
    
    private static final String SMS_APPLICATION_URL = "http://api.eyun.openmas.net/yunmas_api/SendMessageServlet";
	private static final String SMS_APPLICATION_ID = "qyo6h3RTkbTHD0gpZ319qQoZdHC2eSLwM8H";
	private static final String SMS_APPLICATION_PASSWORD = "QvXuKtr0hgPWa2S";
	private static final String SMS_APPLICATION_EXTENDCODE = "3";
    
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
                		System.out.println("开始！");
                		//sendSMS("开始！");
                		sendMail("开始！");
                		break;
                	}
	            }
	            else{
	                System.out.println("错误"+responsecode);
	            }
	        }
        }
        catch(Exception e){
            System.out.println("错误"+e);
        }
    }
    
    public static void sendSMS(String message) throws Exception {
    	HttpClient client = new HttpClient();
    	PostMethod post = new PostMethod(Url); 
    	post.addRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=gbk");
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
    	System.out.println(result); 


    	post.releaseConnection();
    }
    
	private static String SendSms(String[] destinationAddresses, String message) {
		String messageId = null; 
		try {
			System.out.println(" --------------- send message start --------------- ");
			Sms client = new Sms(SMS_APPLICATION_URL);
			messageId = client.SendMessage(destinationAddresses, message, 
					SMS_APPLICATION_EXTENDCODE, SMS_APPLICATION_ID, SMS_APPLICATION_PASSWORD);
			System.out.println(" --------- messageId=" + messageId + " ----------- ");
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
        Properties props = new Properties();
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", "smtp.163.com");
        Session session = Session.getInstance(props);
        session.setDebug(true);
        Message msg = getMimeMessage(session,message);
        Transport transport = session.getTransport();
        transport.connect(senderAccount, senderPassword);
        transport.sendMessage(msg,msg.getAllRecipients());
         
        transport.close();
    }
    
    public static MimeMessage getMimeMessage(Session session,String message) throws Exception{
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(senderAddress));
        msg.setRecipient(MimeMessage.RecipientType.TO,new InternetAddress(recipientAddress));
        msg.setSubject("发送邮件","UTF-8");
        msg.setContent(message, "text/html;charset=UTF-8");
        msg.setSentDate(new Date());
         
        return msg;
    }
}