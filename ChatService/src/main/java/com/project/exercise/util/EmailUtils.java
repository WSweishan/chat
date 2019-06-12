package com.project.exercise.util;

import org.apache.commons.mail.SimpleEmail;

public class EmailUtils {
	public static void sendEmail(String toEmail, String msg) throws Exception{
		SimpleEmail email = new SimpleEmail();
		email.setHostName("smtp.qq.com");
        email.setAuthentication("869277204@qq.com", "obycpeevkbsrbecb");//登录邮箱、密码
        email.setSSLOnConnect(true);
        email.setFrom("869277204@qq.com", "局域网聊天系统邮件");//发送方邮箱
        email.setSubject("用户忘记密码邮件");
        email.setCharset("UTF-8");
        email.setMsg(msg);
        email.addTo(toEmail);
        email.send();
	}
}