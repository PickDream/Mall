package com.mall.test;

import com.mmall.pojo.User;
import com.mmall.util.JsonUtil;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class JsonUtilTest {
    @Test
    public void test(){
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setId(1);
        user1.setUsername("Maoxin1");
        User user2 = new User();
        user2.setId(2);
        user1.setUsername("Maoxin2");
        users.add(user1);
        users.add(user2);
        String prepared = JsonUtil.obj2String(users);
        System.out.println(prepared);

        List<User> users1 = JsonUtil.string2Obj(prepared, new TypeReference<List<User>>() {});
        System.out.println(user1);
        List<User> users2 = JsonUtil.string2Obj(prepared,List.class,User.class);
        System.out.println(user2);
    }
}
