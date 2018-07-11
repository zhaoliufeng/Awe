package com.we_smart.sqldao;

import com.we_smart.sqldao.Annotation.DBFiled;

/**
 *  Created by Zhao Liufeng on 2018/4/19.
 */

public class TestBean {
    @DBFiled(isPrimary = true)
    public String id;
    @DBFiled()
    public String name;
    @DBFiled()
    public boolean boo;

    public int unUseFiled;
}
