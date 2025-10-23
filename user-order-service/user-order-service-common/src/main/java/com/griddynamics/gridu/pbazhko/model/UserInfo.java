package com.griddynamics.gridu.pbazhko.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document("users")
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    @Id
    private String id;

    private String name;
    private String phone;
}
