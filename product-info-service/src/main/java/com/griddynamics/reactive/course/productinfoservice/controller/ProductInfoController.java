package com.griddynamics.reactive.course.productinfoservice.controller;

import com.griddynamics.reactive.course.productinfoservice.domain.Product;
import com.griddynamics.reactive.course.productinfoservice.service.ProductInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/productInfoService")
@RequiredArgsConstructor
public class ProductInfoController {

    private final ProductInfoService productInfoService;

    @GetMapping("/product/names")
    public List<Product> getProductNamesByProductCode(@RequestParam String productCode) {
        return productInfoService.getProductNamesByProductCode(productCode);
    }
}
