package com.fastcampus.projectboard.controller;


import com.fastcampus.projectboard.domain.type.SearchType;
import com.fastcampus.projectboard.dto.response.ArticleResponse;
import com.fastcampus.projectboard.dto.response.ArticleWithCommentsResponse;
import com.fastcampus.projectboard.service.ArticlesService;
import com.fastcampus.projectboard.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;


/*
 * /articles
 * /articles/{article-id}
 * /articles/search
 * /articles/search-hashtag
 * */

// Controller란 사용자가 View에서 어떤 이벤트를 발생시켰을 때, 적절한 이벤트 처리를 하는 역할.
// Model 객체는 컨트롤러에서 생성된 데이터를 담아서 전달하는 목적.
// View 사용자가 보는 화면.
// 이 3개를 뭉쳐서 만든 의미가 MVC
@RequiredArgsConstructor
@RequestMapping("/articles") // 기본 주소
@Controller
public class ArticleController {

    private final ArticlesService articlesService;
    private final PaginationService paginationService;

    @GetMapping
    public String articles(
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size=10, sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map) {

        Page<ArticleResponse> articles = articlesService.searchArticles(searchType,searchValue,pageable).map(ArticleResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(),articles.getTotalPages());

        map.addAttribute("articles", articles);
        map.addAttribute("paginationBarNumbers",barNumbers);

        return "articles/index";
    }

    @GetMapping("/{articleId}") // localhost:8080/articles/{articlesId}
    public String article(@PathVariable Long articleId, ModelMap map) {

        ArticleWithCommentsResponse article = ArticleWithCommentsResponse.from(articlesService.getArticleWithComments(articleId));
        map.addAttribute("article", article);
        map.addAttribute("articleComments", article.articleCommentsResponse());


        return "articles/detail";
    }

}
