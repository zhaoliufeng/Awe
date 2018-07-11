package com.we_smart.sqldao;

import org.junit.Test;

public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {

//        String sql = SqlBuilder.getInstance().createTable(TestBean.class);
//        TestBean bean = new TestBean();
//        System.out.println(sql);
//        bean.id = "1";
//        bean.name = "aa";
//        bean.boo = true;
//        System.out.println(SqlBuilder.getInstance().insertObject(bean));
//        System.out.println(SqlBuilder.getInstance().deleteObject(bean));
//        System.out.println(SqlBuilder.getInstance().updateObject(bean));
//        Integer i = -129, i1 = -129;
//        System.out.println(i == i1);
        TestBean testBean = new TestBean();
        testBean.id = "1";
        testBean.name = "a";
        testBean.boo = true;
        SqlBuilder.getInstance().updateObject(testBean, new String[]{"name", "boo"});
    }
}