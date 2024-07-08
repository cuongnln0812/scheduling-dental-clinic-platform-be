package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.entity.Blog;
import com.example.dentalclinicschedulingplatform.payload.request.BlogCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.BlogUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.BlogDetailResponse;
import com.example.dentalclinicschedulingplatform.payload.response.BlogListResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IBlogService {
    BlogDetailResponse createBlog(BlogCreateRequest request);
    BlogDetailResponse approveBlog(Long id, boolean isApproved);
    BlogDetailResponse viewBlog(Long id);
    Page<BlogListResponse> getAllBlog(int page, int size);
    Page<BlogListResponse> getAllPendingBlog(int page, int size);
    BlogDetailResponse updateBlog(BlogUpdateRequest request);
    BlogDetailResponse removeBlog(Long id);




}
