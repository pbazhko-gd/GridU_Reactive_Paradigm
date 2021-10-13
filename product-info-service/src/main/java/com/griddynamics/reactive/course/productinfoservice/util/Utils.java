package com.griddynamics.reactive.course.productinfoservice.util;

import static java.lang.Thread.sleep;

public class Utils {

    public static void delay(int ms) {
        try {
            sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
