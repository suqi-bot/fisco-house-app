package com.suqi.service;

import com.suqi.pojo.Article;
import com.suqi.pojo.PageBean;


public interface ArticleService{
    //添加文章
    void add(Article article);
    //修改文章
    void update(Article article);
    //条件分页列表查询
    PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state);
}
