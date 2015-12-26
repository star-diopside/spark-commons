package jp.gr.java_conf.star_diopside.spark.commons.web.servlet.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * ルートURIを補完するサーブレットフィルタ
 */
public class RedirectRootUriFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getRequestURI().equals(request.getContextPath())) {
            response.sendRedirect(request.getRequestURI() + "/");
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
