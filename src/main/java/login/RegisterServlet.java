package login;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.sql.*;
import java.util.Date;
import java.util.Properties;

@WebServlet(name = "RegisterServlet" ,urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet{
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
        String psw = request.getParameter("psw");
        try{
            Class.forName("org.sqlite.JDBC");
            Connection connection =DriverManager.getConnection("jdbc:sqlite:E:/JAVA_codes/chest/users.sqlite");
            Statement statement =connection.createStatement();
            statement.executeUpdate("insert into users values ('"+id+"','"+psw+"');");
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
        doPost(request,response);
    }
    private static void sentMail(String receiveMailAccount,String receiveName) throws Exception {
        // 1. 创建参数配置, 用于连接邮件服务器的参数配置
        String myEmailSMTPHost = "smtp.qq.com";
        String myEmailAccount = "528893557@qq.com";
        String myEmailPassword = "hbpayiihruzjcajf";
        Properties props = new Properties();                    // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", myEmailSMTPHost);   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");            // 需要请求认证
        // 开启 SSL 连接, 以及更详细的发送步骤请看上一篇: 基于 JavaMail 的 Java 邮件发送：简单邮件发送
        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getInstance(props);
        session.setDebug(true);                                 // 设置为debug模式, 可以查看详细的发送 log
        // 3. 创建一封邮件
        MimeMessage message = createMimeMessage(session, myEmailAccount, receiveMailAccount,receiveName);
        // 也可以保持到本地查看
        // message.writeTo(file_out_put_stream);
        // 4. 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();
        // 5. 使用 邮箱账号 和 密码 连接邮件服务器
        //    这里认证的邮箱必须与 message 中的发件人邮箱一致，否则报错
        transport.connect(myEmailAccount, myEmailPassword);
        // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(message, message.getAllRecipients());
        // 7. 关闭连接
        transport.close();
    }
    private static MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail,String receiveName) throws Exception {
        // 1. 创建邮件对象
        MimeMessage message = new MimeMessage(session);
        // 2. From: 发件人
        message.setFrom(new InternetAddress(sendMail, "HT的小网站的验证码", "UTF-8"));
        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiveMail, receiveName, "UTF-8"));
        // 4. Subject: 邮件主题
        message.setSubject("HT的小网站的验证码", "UTF-8");
        /*
         * 下面是邮件内容的创建:
         */
        // 5. 创建图片“节点”
        // 6. 创建文本“节点”
        MimeBodyPart text = new MimeBodyPart();
        //    这里添加图片的方式是将整个图片包含到邮件内容中, 实际上也可以以 http 链接的形式添加网络图片
        text.setContent("欢迎注册HT的小网站！<br>您的验证码为5438250", "text/html;charset=UTF-8");
        // 7. （文本+图片）设置 文本 和 图片 “节点”的关系（将 文本 和 图片 “节点”合成一个混合“节点”）
        MimeMultipart mm_text_image = new MimeMultipart();
        mm_text_image.addBodyPart(text);
        mm_text_image.setSubType("related");
        // 12. 设置发件时间
        message.setContent(mm_text_image);
        message.setSentDate(new Date());
        // 13. 保存上面的所有设置
        message.saveChanges();
        return message;
    }
}
