package com.atguigu.servlet;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atguigu.utils.VerifyCodeConfig;

import redis.clients.jedis.Jedis;


/*
 * 1. 在CodeSenderServlet 负责处理生成验证码并通知客户端
 * 页面--->填手机号--->发请求---->后台生成验证码，通知用户
 * 	①获取手机号
 *  ②生成验证码
 *  ③存到数据库中
 *  	获取连接，调api
 *  	Key-value
 *  		key：   和手机号有关
 *  		value: 验证码(String)	
 *  		setex(key,value,expiresec)
 *  
 *  ④通知客户端
 * --->倒计时120秒，用户输入通知的验证码
 * ---->验证---->后台验证--->通知客户端验证是否成功
 * 
  
  2. 要求同一手机号24小时内，最多只能请求三次验证码
  
  		在数据库需要保存手机号在24小时内请求的次数。
  		Key-value：
  			key:      和手机号有关
  			value:    次数string 
  		setex(key,value,24小时)
  		
  	逻辑流程：  在生成验证码之前，判断次数
  			①获取jedis连接
  			②查询当前手机号的次数
  				String result=get(key);
  				a)result==null
  					生成key,设置value为1
  			    b)result==3
  			    	超过次数，不需要再生成验证码
  			    c)1<=x<3
  			    	可以生成，把value+1
  			    	
   3. 总结
   		将redis作为数据库，只需要根据业务建模即可。
   		
   			建模： 数据在redis存储的Key,value的类型
   			
   				调用相应的api即可！
  		
 */
public class CodeSenderServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
   
    public CodeSenderServlet() {
        
    }

    
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获取手机号
		String phone_num = request.getParameter("phone_no");
		
		//统计该手机号发生的次数
		String phone_count = phone_num+VerifyCodeConfig.COUNT_SUFFIX;
		
		
		//设置key
		String key = VerifyCodeConfig.PHONE_PREFIX+phone_num+VerifyCodeConfig.PHONE_SUFFIX;
		
		//生成验证码
		String code = genCode(VerifyCodeConfig.CODE_LEN);
		//存储key和验证码并设置过期时间
		//连接Redis
		Jedis jedis = new Jedis(VerifyCodeConfig.HOST, VerifyCodeConfig.PORT, 6000);
		//存储数据
		jedis.setex(key, VerifyCodeConfig.CODE_TIMEOUT, code);
		
		//关闭连接
		jedis.close();
		//打印验证码
		System.out.println(code);
		//打印key和验证码
		System.out.println(key+"  "+code);
		
	} 
	
	
	//生成6位验证码
	private  String genCode(int len){
		 String code="";
		 for (int i = 0; i < len; i++) {
		     int rand=  new Random().nextInt(10);
		     code+=rand;
		 }
		 
		return code;
	}
	
	
 
}
