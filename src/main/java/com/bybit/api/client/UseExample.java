package com.bybit.api.client;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.service.BybitApiClientFactory;

public class UseExample {

    public static void main(String[] args) {

        String key = "key";
        String secret = "secret";

        testRest(key, secret);
        testPubWss(key, secret);
        testPriWss(key, secret);

    }

    public static void testRest(String key, String secret) {
        BybitRestWrapper restWrapper = new BybitRestWrapper(key, secret);
        restWrapper.getAccountInfo();
        restWrapper.getPositionInfo();
    }

    public static void testPubWss(String key, String secret) {
        // 每次subscribe一个新的channel会自动创建一个新的wss连接对象，详情参考WebsocketClientImpl。
        // 为了正确记录WebsocketClientImpl中的argnames，因此每一个新的subscribe需要一个新对象
        // public
        BybitWssWrapper bybitWssWrapper = new BybitWssWrapper(key, secret, false);
//        bybitWssWrapper.subTicker("BTCUSDT", "ETHUSDT");
//        BybitWssWrapper bybitWssWrapper1 = new BybitWssWrapper(key, secret, false);
//        bybitWssWrapper1.subOrderBook50("BTCUSDT", "ETHUSDT");
        // 运用multi可以一个client订阅多个channel
        bybitWssWrapper.subPubMultiChanel("tickers.BTCUSDT", "orderbook.50.ETHUSDT");
    }

    public static void testPriWss(String key, String secret) {
        BybitWssWrapper bybitWssWrapper = new BybitWssWrapper(key, secret, true);
        // 运用multi可以一个client订阅多个channel
        bybitWssWrapper.subPriMultiChanel("position", "order");
//        BybitWssWrapper bybitWssWrapper1 = new BybitWssWrapper(key, secret, true);
//        bybitWssWrapper1.subOrder();

    }
}
