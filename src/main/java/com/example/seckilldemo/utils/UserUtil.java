package com.example.seckilldemo.utils;

import com.baomidou.mybatisplus.core.toolkit.SystemClock;
import com.example.seckilldemo.entity.TUser;
import com.example.seckilldemo.vo.RespBean;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 生成用户工具类
 *
 * @author: LC
 * @date 2022/3/4 3:29 下午
 * @ClassName: UserUtil
 */
public class UserUtil {
    private static void createUser(int count) throws Exception {
        Date date = new Date(System.currentTimeMillis());
        List<TUser> users = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            TUser user = new TUser();
            user.setId(1300000000L + i);
            user.setNickname("user" + i);
            user.setSalt("1a2b3c");
            user.setPassword(MD5Util.inputPassToDBPass("123456", user.getSalt()));
            user.setLoginCount(1);
            user.setRegisterDate(date);
            users.add(user);
        }
        System.out.println("create user");

//        Connection conn = getConn();
//        String sql = "insert into t_user(login_count,nickname,register_date,salt,password,id) value(?,?,?,?,?,?)";
//        PreparedStatement pstmt = conn.prepareStatement(sql);
//        conn.prepareStatement(sql);
//        for (int i = 0; i < users.size(); i++) {
//            TUser user = users.get(i);
//            pstmt.setInt(1, user.getLoginCount());
//            pstmt.setString(2, user.getNickname());
//            pstmt.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
//            pstmt.setString(4, user.getSalt());
//            pstmt.setString(5, user.getPassword());
//            pstmt.setLong(6, user.getId());
//            pstmt.addBatch();
//        }
//        pstmt.executeBatch();
//        pstmt.clearParameters();
//        conn.close();
//        System.out.println("insert to db");
        //登录，生成userTicket
        String urlString="http://192.168.174.130:8080/login/toLogin";
        File file=new File("C:\\Users\\Administrator\\Desktop\\config.txt");
        if(file.exists()){
            file.delete();
        }
        RandomAccessFile raf=new RandomAccessFile(file,"rw");
        raf.seek(0);
        for (int i = 0; i < users.size(); i++) {
            TUser user=users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params="mobile="+user.getId()+"&password="+MD5Util.inputPassToFromPass("123456");
            System.out.println("user.getId()=== "+user.getId());
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len=0;
            while ((len=inputStream.read(buff))>=0){
                bout.write(buff,0,len);
            }
            inputStream.close();
            bout.close();
            String respone=new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(respone, RespBean.class);
            String userTicket = (String) respBean.getObject();
            System.out.println("create userTicket: "+user.getId());
            System.out.println("create userTicket: "+userTicket);
            String row=user.getId()+","+userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
//            System.out.println("Write to file :"+user.getId());
        }
        raf.close();
        System.out.println("over");



    }

    private static Connection getConn() throws Exception {
        String url = "jdbc:mysql://192.168.174.130:3306/seckill?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "123";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    public static void main(String[] args) throws Exception {
        createUser(5000);
    }
}
