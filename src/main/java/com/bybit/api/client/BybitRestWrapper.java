package com.bybit.api.client;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.TradeOrderType;
import com.bybit.api.client.domain.asset.request.AssetDataRequest;
import com.bybit.api.client.domain.position.request.PositionDataRequest;
import com.bybit.api.client.domain.trade.PositionIdx;
import com.bybit.api.client.domain.trade.Side;
import com.bybit.api.client.domain.trade.TimeInForce;
import com.bybit.api.client.domain.trade.request.PlaceOrderRequest;
import com.bybit.api.client.domain.trade.request.TradeOrderRequest;
import com.bybit.api.client.restApi.*;
import com.bybit.api.client.service.BybitApiClientFactory;

import java.util.Map;

public class BybitRestWrapper {
    private String apiKey;
    private String secret;

    private String baseUrl;

    private BybitApiAsyncTradeRestClient tradeClient;

    private BybitApiAsyncPositionRestClient posClient;

    private BybitApiAsyncAccountRestClient accClient;


    public BybitRestWrapper(String key, String secret) {
        // Async mode
        tradeClient = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN, true)
                .newAsyncTradeRestClient();
        posClient = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN)
                .newAsyncPositionRestClient();
        accClient = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN)
                .newAsyncAccountRestClient();
    }

    // https://bybit-exchange.github.io/docs/v5/order/create-order
    public void placeOrder() {
        TradeOrderRequest newOrderRequest = TradeOrderRequest.builder().category(CategoryType.SPOT).symbol("BTCUSDT")
                .side(Side.BUY).orderType(TradeOrderType.LIMIT).qty("0.1").timeInForce(TimeInForce.POST_ONLY).price("60000.0")
                .orderLinkId("spot-test").isLeverage(0).build();
        tradeClient.createOrder(newOrderRequest, System.out::println);
    }

    // https://bybit-exchange.github.io/docs/v5/position
    public void getPositionInfo() {
        PositionDataRequest positionListRequest = PositionDataRequest.builder().category(CategoryType.LINEAR).symbol("BTCUSDT").build();
        posClient.getPositionInfo(positionListRequest, System.out::println);
    }

    // https://bybit-exchange.github.io/docs/v5/account/account-info
    public void getAccountInfo() {
        accClient.getAccountInfo(System.out::println);
    }




}
