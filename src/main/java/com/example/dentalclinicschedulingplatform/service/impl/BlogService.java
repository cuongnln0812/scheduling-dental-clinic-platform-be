package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.request.BlogCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.BlogUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.BlogDetailResponse;
import com.example.dentalclinicschedulingplatform.payload.response.BlogListResponse;
import com.example.dentalclinicschedulingplatform.repository.*;
import com.example.dentalclinicschedulingplatform.service.IAuthenticateService;
import com.example.dentalclinicschedulingplatform.service.IBlogService;
import com.example.dentalclinicschedulingplatform.service.IMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlogService implements IBlogService {

    private final ClinicBranchRepository branchRepository;
    private final ClinicRepository clinicRepository;
    private final StaffRepository staffRepository;
    private final IAuthenticateService authenticateService;
    private final BlogRepository blogRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final IMailService mailService;
    @Override
    public BlogDetailResponse createBlog(BlogCreateRequest request) {
        try {
                if(!authenticateService.getUserInfo().getRole().equals(UserType.STAFF.toString())) throw new ApiException(HttpStatus.BAD_REQUEST, "You do not have permission");
            ClinicStaff staff = staffRepository.findByUsername(authenticateService.getUserInfo().getUsername())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Staff not found"));
            Clinic clinic = staff.getClinicBranch().getClinic();
            if(Objects.isNull(clinic)) throw new ApiException(HttpStatus.NOT_FOUND, "Clinic not found");

            Blog blog = new Blog();
            modelMapper.map(request, blog);
            blog.setStatus(ClinicStatus.PENDING);
            blog.setCreatedBy(staff.getFullName());
            blog.setCreatedDate(LocalDateTime.now());
            blog.setClinic(clinic);

            blogRepository.save(blog);
            return modelMapper.map(blog, BlogDetailResponse.class);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public BlogDetailResponse approveBlog(Long id, boolean isApproved) {
        try {
            if(!authenticateService.getUserInfo().getRole().equals(UserType.ADMIN.toString()))
                throw new ApiException(HttpStatus.BAD_REQUEST, "You do not have permission");
            Blog blog = blogRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Blog not found"));
            if(!blog.getStatus().equals(ClinicStatus.PENDING)) throw new ApiException(HttpStatus.BAD_REQUEST, "Blog is not pending");
            if(isApproved){
                blog.setStatus(ClinicStatus.ACTIVE);
                blog.setPublishDate(LocalDate.now());
            } else {
                blog.setStatus(ClinicStatus.DENIED);
            }

            blog = blogRepository.save(blog);
            return modelMapper.map(blog, BlogDetailResponse.class);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public BlogDetailResponse viewBlog(Long id) {
        try {
            Blog blog = blogRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Blog not found"));
            //if(!blog.getStatus().equals(ClinicStatus.ACTIVE)) throw new ApiException(HttpStatus.BAD_REQUEST, "Blog is not active");

            return modelMapper.map(blog, BlogDetailResponse.class);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Page<BlogDetailResponse> getAllBlog(int page, int size) {
        try {
            Pageable pageRequest = PageRequest.of(page, size);
            Page<Blog> listBlog;
            listBlog = blogRepository.findAllByStatus(ClinicStatus.ACTIVE, pageRequest);
            return listBlog.map(blog ->
                    new BlogDetailResponse(blog.getId(), blog.getTitle(),
                            blog.getSummary(), blog.getContent(),
                            blog.getThumbnail(), blog.getPublishDate(),
                            blog.getStatus(), blog.getCreatedBy(),
                            blog.getCreatedDate(), blog.getModifiedBy(),
                            blog.getModifiedDate(), blog.getClinic().getClinicName()));
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Page<BlogDetailResponse> getAllPendingBlog(int page, int size) {
        try {
            Pageable pageRequest = PageRequest.of(page, size);
            Page<Blog> listBlog;
            listBlog = blogRepository.findAllByStatus(ClinicStatus.PENDING, pageRequest);
            return listBlog.map(blog ->
                    new BlogDetailResponse(blog.getId(), blog.getTitle(),
                            blog.getSummary(), blog.getContent(),
                            blog.getThumbnail(), blog.getPublishDate(),
                            blog.getStatus(), blog.getCreatedBy(),
                            blog.getCreatedDate(), blog.getModifiedBy(),
                            blog.getModifiedDate(), blog.getClinic().getClinicName()));
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public BlogDetailResponse updateBlog(BlogUpdateRequest request) {
        try {
            Blog blog = blogRepository.findById(request.getId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Blog not found"));
            if(StringUtils.isNotBlank(request.getTitle())) {
                blog.setTitle(request.getTitle());
            }
            if(StringUtils.isNotBlank(request.getSummary())) {
                blog.setSummary(request.getSummary());
            }
            if(StringUtils.isNotBlank(request.getContent())) {
                blog.setContent(request.getContent());
            }
            if(StringUtils.isNotBlank(request.getThumbnail())) {
                blog.setThumbnail(request.getThumbnail());
            }

            blog = blogRepository.save(blog);
            return modelMapper.map(blog, BlogDetailResponse.class);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public BlogDetailResponse removeBlog(Long id) {
        try {
            Blog blog = blogRepository.findById(id)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Blog not found"));
            if(blog.getStatus().equals(ClinicStatus.INACTIVE)) throw new ApiException(HttpStatus.CONFLICT, "Blog is already inactive");
            blog.setStatus(ClinicStatus.INACTIVE);

            blog = blogRepository.save(blog);
            return modelMapper.map(blog, BlogDetailResponse.class);
        } catch (Exception e) {
            throw e;
        }
    }
}
