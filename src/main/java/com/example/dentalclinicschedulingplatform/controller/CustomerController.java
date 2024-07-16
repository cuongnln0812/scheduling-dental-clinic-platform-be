package com.example.dentalclinicschedulingplatform.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Customer Controller")
@RequiredArgsConstructor
public class CustomerController {

}
