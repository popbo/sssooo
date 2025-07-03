package com.stdc.visual.auth.utils;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author: wang_jie
 * @data: 2022/7/8--15:26
 * @describe: 一些常用的单例对象
 */
public class Holder {

    /**
     * RANDOM
     */
    public final static Random RANDOM = new Random();

    /**
     * SECURE_RANDOM
     */
    public final static SecureRandom SECURE_RANDOM = new SecureRandom();
}
