package com.quark.cobra.domain;

import lombok.Data;

@Data
public class BaseReqBo {
    private String tenantId;
    private String clientId;
    private String productId;
}
