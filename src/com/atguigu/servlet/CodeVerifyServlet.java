package com.atguigu.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.codec.binary.StringUtils;

import com.atguigu.utils.VerifyCodeConfig;

import redis.clients.jedis.Jedis;

//验证手机验证码
public class CodeVerifyServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public CodeVerifyServlet() {

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// 接收客户端输入的手机号和验证码
		String phone_num = request.getParameter("phone_no");
		String code_re = request.getParameter("verify_code");
		
		
		// 生成Key
		String key = VerifyCodeConfig.PHONE_PREFIX+phone_num+VerifyCodeConfig.PHONE_SUFFIX;
		// 获取jedis连接，根据key，查询数据库中的验证码
		Jedis jedis = new Jedis(VerifyCodeConfig.HOST, VerifyCodeConfig.PORT, 6000);
		String code = jedis.get(key);
		// 对比判断，响应页面
		if(code.equals(code_re)) {
			response.getWriter().print(true);
		}else {
			response.getWriter().print(false);
		}
		

	}

}
