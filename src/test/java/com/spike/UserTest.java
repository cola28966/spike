package com.spike;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.spike.model.MiaoshaUser;
import com.spike.service.MiaoshaUserService;
import com.spike.util.MD5Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = SpikeApplication.class)
public class UserTest {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Test
    public  void createUsers()  {
        Long userId = 18902110000L;
        String urlString = "http://localhost:8080/login/do_login";
        FileOutputStream fos = null;
        OutputStreamWriter osw  = null;
        BufferedWriter bw = null;

        try {
            fos = new FileOutputStream("D:/result.txt");
            osw = new OutputStreamWriter(fos,"utf-8");
            bw = new BufferedWriter(osw);
            for(int i = 0;i<1000;++i){
                MiaoshaUser user = new MiaoshaUser();
                user.setId(userId+i);
                user.setLoginCount(1);
                user.setNickname("user"+i);
                user.setRegisterDate(new Date());
                user.setSalt("1a2b3c");
                user.setPassword(MD5Util.inputPassToDbPass("123456", user.getSalt()));
                System.out.println(user);

                URL url = new URL(urlString);
                HttpURLConnection co = (HttpURLConnection)url.openConnection();

                co.setRequestMethod("POST");
                co.setDoOutput(true);
                OutputStream out = co.getOutputStream();
                String params = "mobile="+user.getId()+"&password="+MD5Util.inputPassFormPass("123456");
                out.write(params.getBytes());
                out.flush();
                InputStream inputStream = co.getInputStream();
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                byte buff[] = new byte[1024];
                int len = 0;
                while((len = inputStream.read(buff)) >= 0) {
                    bout.write(buff, 0 ,len);
                }
                inputStream.close();
                bout.close();
                String response = new String(bout.toByteArray());
                JSONObject jo = JSON.parseObject(response);
                String token = jo.getString("data");

                String row = user.getId()+","+token;
                bw.write(row+"\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}
