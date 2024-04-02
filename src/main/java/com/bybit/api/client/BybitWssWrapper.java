package com.bybit.api.client;

import com.alibaba.fastjson.JSONArray;
import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.websocket_message.public_channel.PublicOrderBookData;
import com.bybit.api.client.domain.websocket_message.public_channel.PublicTickerData;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.bybit.api.client.websocket.WebsocketClient;
import com.bybit.api.client.websocket.WebsocketMessageHandler;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.Validate;

import javax.sql.rowset.spi.SyncResolver;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BybitWssWrapper {

    private String key;
    private String secret;

    private WebsocketMessageHandler messageHandler;
    private String maxAliveTime;

    private WebsocketClient client;


    public BybitWssWrapper(String key, String secret, Boolean isPrivate) {
        if (isPrivate) {
            this.key = key;
            this.secret = secret;
            // 可以设定具体的messageHandler函数
            client = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.STREAM_MAINNET_DOMAIN).newWebsocketClient(response -> {
                System.out.println("private response is " + response);
            });
        } else {
            // STREAM_MAINNET_DOMAIN mean wesocket
            client = BybitApiClientFactory.newInstance(BybitApiConfig.STREAM_MAINNET_DOMAIN, false)
                    .newWebsocketClient();
            client.setMessageHandler(response -> {
//                System.out.println("public response is " + response);
                decryptPublic(response);
            });
        }
    }

    // https://bybit-exchange.github.io/docs/v5/websocket/public/ticker
    public void subTicker(String... symbols) {
        List<String> syms = new ArrayList<>();
        for (String symbol : symbols) {
            syms.add("tickers."+symbol);
        }
        client.getPublicChannelStream(syms, BybitApiConfig.V5_PUBLIC_SPOT);
    }

    // https://bybit-exchange.github.io/docs/v5/websocket/public/orderbook
    public void subOrderBook50(String... symbols) {
        List<String> syms = new ArrayList<>();
        for (String symbol : symbols) {
            syms.add("orderbook.50."+symbol);
        }
        client.getPublicChannelStream(syms, BybitApiConfig.V5_PUBLIC_SPOT);
    }

    public void subPubMultiChanel(String... channel) {
        List<String> ch = new ArrayList<>();
        for (String c : channel) {
            ch.add(c);
        }
        client.getPublicChannelStream(ch, BybitApiConfig.V5_PUBLIC_SPOT);
    }

    // https://bybit-exchange.github.io/docs/v5/websocket/private/position
    public void subPosition() {
        Validate.notNull(key);
        Validate.notNull(secret);
        client.getPrivateChannelStream(List.of("position"), BybitApiConfig.V5_PRIVATE);
    }

    // https://bybit-exchange.github.io/docs/v5/websocket/private/order
    public void subOrder() {
        Validate.notNull(key);
        Validate.notNull(secret);
        client.getPrivateChannelStream(List.of("order"), BybitApiConfig.V5_PRIVATE);
    }

    public void subPriMultiChanel(String... channel) {
        List<String> ch = new ArrayList<>();
        for (String c : channel) {
            ch.add(c);
        }
        System.out.println(ch);
        client.getPrivateChannelStream(ch, BybitApiConfig.V5_PRIVATE);
    }

    private void decryptPublic(final String msg) {
        JSONObject json = JSONObject.parseObject(msg);
        if(json.get("data")!=null) {
            final String topic = json.getString("topic");
            final String dataMsg = json.getString("data");
            // "instType":"SPOT","instId":"BTCUSDT","channel":"ticker"
            final Long ts = json.getLong("ts");
            try {
                if(topic.contains("tickers")) {
                    PublicTickerData data = (new ObjectMapper()).readValue(dataMsg, PublicTickerData.class);
                    final String symbol = data.getSymbol();
                    final String lastPrice = data.getLastPrice();
                    final String highPrice24h = data.getHighPrice24h();
                    final String lowPrice24h = data.getLowPrice24h();
                    final String volume24h = data.getVolume24h();
                    final String turnover24h = data.getTurnover24h();
                    System.out.println(new StringBuilder()
                            .append("bybit,").append(topic)
                            .append(",").append(lastPrice)
                            .append(",").append(highPrice24h)
                            .append(",").append(lowPrice24h)
                            .append(",").append(volume24h)
                            .append(",").append(turnover24h)
                            .append(",").append(ts).toString());
                } else if (topic.contains("orderbook")) {
                    PublicOrderBookData data = (new ObjectMapper()).readValue(dataMsg, PublicOrderBookData.class);
                    final String type = json.getString("type");
                    final String symbol = data.getS();
                    final List<List<String>> bids = data.getB();
                    if (bids == null || bids.isEmpty()) {
                        // 50太快了，可能无法接收到某一部分的消息
//                    System.out.println("bids size 0");
                        return;
                    }
                    final List<String> bid1 = bids.get(0);
                    final String bid1Px = bid1.get(0);
                    final String bid1Sz = bid1.get(1);
                    final List<List<String>> asks = data.getA();
                    if (asks == null ||asks.isEmpty()) {
//                    System.out.println("asks size 0");
                            return;
                    }
                    final List<String> ask1 = asks.get(0);
                    final String ask1Px = ask1.get(0);
                    final String ask1Sz = ask1.get(1);
                    System.out.println(new StringBuilder()
                            .append("bybit,").append(topic)
                            .append(",").append(type)
                            .append(",").append(bid1Px)
                            .append(",").append(bid1Sz)
                            .append(",").append(ask1Px)
                            .append(",").append(ask1Sz)
                            .append(",").append(ts).toString());
                }

            } catch (JsonProcessingException e) {
                System.out.println(e);

            }

        }

    }

}
