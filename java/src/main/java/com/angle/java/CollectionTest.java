package com.angle.java;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 测试集合的类
 */
public class CollectionTest {

    static int DEFAULT = 10000000;
    static long startTime;

    public static void main(String[] args) {

        List<Integer> mArrayList = new ArrayList<>();
        List<Integer> mLinkedList = new LinkedList<>();

        /*
         * 数据量达到10000000时
         * ArrayList添加数据时间为1977
         * LinkedList添加数据时间为4999
         */
        startTime = System.currentTimeMillis();
        for (int i = 0; i < DEFAULT; i++) {
            mArrayList.add(i);
        }
        System.out.println("ArrayList添加数据时间为" + (System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();
        for (int i = 0; i < DEFAULT; i++) {
            mLinkedList.add(i);
        }
        System.out.println("LinkedList添加数据时间为" + (System.currentTimeMillis() - startTime));

//        /*
//         * ArrayList查找数据所需时间0
//         * LinkedList查找数据所需时间1
//         */
//        startTime = System.currentTimeMillis();
//        mArrayList.get(900000);
//        System.out.println("ArrayList查找数据所需时间" + (System.currentTimeMillis() - startTime));
//
//        startTime = System.currentTimeMillis();
//        mLinkedList.get(900000);
//        System.out.println("LinkedList查找数据所需时间" + (System.currentTimeMillis() - startTime));


//        /*
//         * ArrayList插入数据所需时间0
//         * LinkedList插入数据所需时间0
//         */
//        startTime = System.currentTimeMillis();
//        mArrayList.set(1345, 9999999);
//        System.out.println("ArrayList插入数据所需时间" + (System.currentTimeMillis() - startTime));
//
//        startTime = System.currentTimeMillis();
//        mLinkedList.set(1345, 9999999);
//        System.out.println("LinkedList插入数据所需时间" + (System.currentTimeMillis() - startTime));


        /*
         * 删除数据比较明显
         * ArrayList删除数据所需时间4
         * LinkedList删除数据所需时间0
         */
        startTime = System.currentTimeMillis();
        mArrayList.remove(1345);
        System.out.println("ArrayList删除数据所需时间" + (System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();
        mLinkedList.remove(1345);
        System.out.println("LinkedList删除数据所需时间" + (System.currentTimeMillis() - startTime));

    }
}
