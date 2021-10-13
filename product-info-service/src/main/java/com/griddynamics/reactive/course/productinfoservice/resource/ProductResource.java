package com.griddynamics.reactive.course.productinfoservice.resource;

import com.griddynamics.reactive.course.productinfoservice.domain.Product;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

@Repository
public class ProductResource {

    private final Random random = new Random();

    public List<Product> getProductNamesByProductCode(String productCode) {
        return List.of(new Product("111", productCode, "IceCream", randomDouble()),
                new Product("222", productCode, "Milk", randomDouble()),
                new Product("333", productCode, "Meal", randomDouble()),
                new Product("444", productCode, "Apple", randomDouble()));
    }

    private double randomDouble() {
        int min = 100;
        int max = 10000;
        double value =  min + (max - min) * random.nextDouble();
        BigDecimal bigDecimalValue = BigDecimal.valueOf(value);
        bigDecimalValue = bigDecimalValue.setScale(2, RoundingMode.HALF_UP);
        return bigDecimalValue.doubleValue();
    }
}
