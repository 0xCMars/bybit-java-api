package com.bybit.api.client.domain.spot.marginTrade;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class SpotMarginTradeBorrowOrdersRequest {
    private final Long startTime;
    private final Long endTime;
    private final String coin;
    private final Integer status;
    private final Integer limit;
}

