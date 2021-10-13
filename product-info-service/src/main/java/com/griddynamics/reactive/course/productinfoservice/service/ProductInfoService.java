package com.griddynamics.reactive.course.productinfoservice.service;

import com.griddynamics.reactive.course.productinfoservice.domain.Product;
import com.griddynamics.reactive.course.productinfoservice.resource.ProductResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

import static com.griddynamics.reactive.course.productinfoservice.util.Utils.delay;

@Service
@RequiredArgsConstructor
public class ProductInfoService {

    private final ProductResource productResource;
    private final Random random = new Random();

    public List<Product> getProductNamesByProductCode(String productCode) {
        delay(randomInt());
        return productResource.getProductNamesByProductCode(productCode);
    }

    private int randomInt() {
        int min = 1000;
        int max = 10000;
        return random.nextInt(max - min) + min;
    }
}
