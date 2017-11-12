package com.wxtools.util;

import com.wxtools.entity.Contact;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2017/10/21.
 */
public class JdbcUtil {
    public static void insertDb(List<Contact> contactList){
        System.out.println(contactList.size());
        Connection connection = getConn();
        try {
            connection.setAutoCommit(false);
            String sql = "insert into contact(alias,app_account_flag,attr_status,chat_rom_id,city,contact_flag,display_name,encry_chat_room_id,head_img_url,hide_input_bar_flag,is_owner,key_word,member_count,nick_name,owner_uin,py_initial,py_quan_pin,province,remark_name,remark_py_initial,remark_py_quan_pin,sex,signature,sns_flag,star_friend,statues,uin,uni_friend,user_name,verify_flag) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            contactList.stream()
                    .forEach((Contact contact) -> {
                        try {
                            preparedStatement.setString(1,contact.getAlias());
                            preparedStatement.setLong(2,contact.getAppAccountFlag());
                            preparedStatement.setLong(3,contact.getAttrStatus());
                            preparedStatement.setLong(4,contact.getChatRoomId());
                            preparedStatement.setString(5,contact.getCity());
                            preparedStatement.setLong(6,contact.getContactFlag());
                            preparedStatement.setString(7,contact.getDisplayName());
                            preparedStatement.setString(8,contact.getEncryChatRoomId());
                            preparedStatement.setString(9,contact.getHeadImgUrl());
                            preparedStatement.setLong(10,contact.getHideInputBarFlag());
                            preparedStatement.setLong(11,contact.getIsOwner());
                            preparedStatement.setString(12,contact.getKeyWord());
                            preparedStatement.setLong(13,contact.getMemberCount());
                            preparedStatement.setString(14,contact.getNickName());
                            preparedStatement.setLong(15,contact.getOwnerUin());
                            preparedStatement.setString(16,contact.getPYInitial());
                            preparedStatement.setString(17,contact.getPYQuanPin());
                            preparedStatement.setString(18,contact.getProvince());
                            preparedStatement.setString(19,contact.getRemarkName());
                            preparedStatement.setString(20,contact.getRemarkPYInitial());
                            preparedStatement.setString(21,contact.getRemarkPYQuanPin());
                            preparedStatement.setLong(22,contact.getSex());
                            preparedStatement.setString(23,contact.getSignature());
                            preparedStatement.setLong(24,contact.getSnsFlag());
                            preparedStatement.setLong(25,contact.getStarFriend());
                            preparedStatement.setLong(26,contact.getStatues());
                            preparedStatement.setLong(27,contact.getUin());
                            preparedStatement.setLong(28,contact.getUniFriend());
                            preparedStatement.setString(29,contact.getUserName());
                            preparedStatement.setLong(30,contact.getVerifyFlag());
                            preparedStatement.addBatch();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
            preparedStatement.executeBatch();
            connection.commit();
            preparedStatement.clearBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static Connection getConn() {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/test_mysql";
        String username = "root";
        String password = "root";
        Connection conn = null;
        try {
            Class.forName(driver); //classLoader,加载对应驱动
            conn = (Connection) DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
