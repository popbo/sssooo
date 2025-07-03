package com.stdc.visual;

import com.stdc.visual.auth.entity.user.po.SysUser;
import com.stdc.visual.auth.mapper.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.concurrent.ExecutionException;

/**
 * @author: wang_jie
 * @data: 2022/5/23--20:12
 * @describe:
 */
@SpringBootTest
public class StdcVisualApiTest {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 注册用户
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    void regist() throws ExecutionException, InterruptedException {
        SysUser user = new SysUser();
        user.setUsername("wangjie");
        String psw = passwordEncoder.encode("123");
        user.setPassword(psw);
        sysUserMapper.insert(user);
    }

}
