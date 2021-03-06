package com.autumnframework.blog.controller;

import com.autumnframework.blog.dao.mongo.BlogRepository;
import com.autumnframework.blog.model.document.BlogDetail;
import com.autumnframework.common.architect.constant.ResponseCode;
import com.autumnframework.common.architect.utils.ResponseMsgUtil;
import com.autumnframework.common.model.bo.DataPageResponseMsg;
import com.autumnframework.common.model.po.ArticleInfo;
import com.autumnframework.common.service.interfaces.IArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

/**
 * @author Junlan Shuai[shuaijunlan@gmail.com].
 * @date Created on 17:23 2018/4/20.
 */
@Controller
@RequestMapping(value = "article")
public class ArticleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);
    private BlogRepository blogRepository;
    private IArticleService articleService;
    public ArticleController(BlogRepository blogRepository, IArticleService articleService){
        this.blogRepository = blogRepository;
        this.articleService =articleService;
    }
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String getBlogDetail(@PathVariable("id") String id, Model model){

        Optional<BlogDetail> blog =  blogRepository.findById(id);

        if (blog.isPresent()){
            // increment visiting times by one
            BlogDetail blogDetail = blog.get();

            articleService.updateVisitTimes(0, blogDetail.getId());
            List<ArticleInfo> articleInfo = articleService.getArticleByVisitId(blogDetail.getId());
            if (LOGGER.isInfoEnabled()){
                LOGGER.info("Blog detail:{}", blogDetail);
            }
            if (articleInfo.size() != 0){
                model.addAttribute("title", articleInfo.get(0).getTitle());
                model.addAttribute("username", articleInfo.get(0).getUser_name());
                model.addAttribute("time", articleInfo.get(0).getPost_time());
                model.addAttribute("visit_times", articleInfo.get(0).getVisit_times());
                model.addAttribute("content", blogDetail.getContent_md());
            }

        }
        return "detail";
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public DataPageResponseMsg getArticleList(){
        List<ArticleInfo> list = articleService.getArticleList();
        int count = list.size();
        return ResponseMsgUtil.returnCodeMessage(ResponseCode.REQUEST_SUCCESS, list, count);
    }
}
