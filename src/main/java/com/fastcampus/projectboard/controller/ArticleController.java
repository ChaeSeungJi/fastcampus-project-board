package com.fastcampus.projectboard.controller;


import com.fastcampus.projectboard.domain.constant.FormStatus;
import com.fastcampus.projectboard.domain.type.SearchType;
import com.fastcampus.projectboard.dto.request.ArticleRequest;
import com.fastcampus.projectboard.dto.response.ArticleResponse;
import com.fastcampus.projectboard.dto.response.ArticleWithCommentsResponse;
import com.fastcampus.projectboard.dto.security.BoardPrincipal;
import com.fastcampus.projectboard.service.ArticlesService;
import com.fastcampus.projectboard.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

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

        // 타임리프에서 articleResponse객체를 보고 필드를 불러오면 안됨 실제 articleService는 ArticleDto를 참조하기 때문에 ArticleDto의 필드를 불러야함.

        map.addAttribute("articles", articles);
        map.addAttribute("paginationBarNumbers",barNumbers);
        map.addAttribute("searchTypes",SearchType.values());

        return "articles/index";
    }

    @GetMapping("/{articleId}") // localhost:8080/articles/{articlesId}
    public String article(@PathVariable Long articleId, ModelMap map) {

        ArticleWithCommentsResponse article = ArticleWithCommentsResponse.from(articlesService.getArticleWithComments(articleId));
        map.addAttribute("article", article);
        map.addAttribute("articleComments", article.articleCommentsResponse());


        return "articles/detail";
    }

    @GetMapping("/form")
    public String articleForm(ModelMap map) {
        map.addAttribute("formStatus", FormStatus.CREATE);

        return "articles/form";
    }

    @PostMapping("/form")
    public String postNewArticle(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            ArticleRequest articleRequest
    ) {
        articlesService.saveArticle(articleRequest.toDto(boardPrincipal.toDto()));

        return "redirect:/articles";
    }

    @GetMapping("/{articleId}/form")
    public String updateArticleForm(@PathVariable Long articleId, ModelMap map) {
        ArticleResponse article = ArticleResponse.from(articlesService.getArticle(articleId));

        map.addAttribute("article", article);
        map.addAttribute("formStatus", FormStatus.UPDATE);

        return "articles/form";
    }

    @PostMapping("/{articleId}/form")
    public String updateArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            ArticleRequest articleRequest
    ) {
        articlesService.updateArticle(articleId, articleRequest.toDto(boardPrincipal.toDto()));

        return "redirect:/articles/" + articleId;
    }

    @PostMapping("/{articleId}/delete")
    public String deleteArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        articlesService.deleteArticle(articleId, boardPrincipal.getUsername());

        return "redirect:/articles";
    }

}
