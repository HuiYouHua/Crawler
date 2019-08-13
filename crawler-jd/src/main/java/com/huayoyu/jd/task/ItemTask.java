package com.huayoyu.jd.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huayoyu.jd.pojo.Item;
import com.huayoyu.jd.service.ItemService;
import com.huayoyu.jd.utils.HttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ItemTask {

    @Autowired
    private HttpUtils httpUtils;

    @Autowired
    private ItemService itemService;

    private static final ObjectMapper mapper = new ObjectMapper();

    // 当下载任务完成后, 间隔多长时间进行下一次任务
    @Scheduled(fixedDelay = 100*1000)
    public void itemTask() throws Exception {
        String url = "http://search.jd.com/Search?keyword=手机&enc=utf-8&qrst=1&rt=1" +
                "&stop=1&vt=2&wq=shou%27ji&cid2=653&cid3=655&s=57&click=0&page=";
        for (int i = 1; i < 10; i=i+2) {
            String html = httpUtils.doGetHtml(url + i);
            System.out.println(html);
            this.parse(html);
        }

        System.out.println("手机数据抓取完成");
    }

    private void parse(String html) throws Exception {
        Document document = Jsoup.parse(html);

        Elements elements = document.select("div#J_goodsList > ul > li");
        for(Element spuEle: elements) {
            long spu = Long.parseLong(spuEle.attr("data-spu"));

            // 获取sku
            Elements skuEles = spuEle.select("li.ps-item");

            for (Element skuEle : skuEles) {
                long sku = Long.parseLong(skuEle.select("[data-sku]").attr("data-sku"));

                Item item = new Item();
                item.setSku(sku);
                List<Item> list = itemService.findAll(item);

                if (list.size() > 0) {
                    continue;
                }

                item.setSpu(spu);

                String itemUrl = "https://item.jd.com/"+ sku +".html";
                item.setUrl(itemUrl);

                String picUrl = "https:" + skuEle.select("img[data-sku]").first().attr("data-lazy-img");
                picUrl = picUrl.replace("/n9/", "/n1/");
                String picName = httpUtils.doGetImage(picUrl);
                item.setPic(picName);

                String priceJson = httpUtils.doGetHtml("https://p.3.cn/prices/mgets?skuIds=J_"+ sku);
                double price = mapper.readTree(priceJson).get(0).get("p").asDouble();
                item.setPrice(price);

                String itemInfo = httpUtils.doGetHtml(item.getUrl());
                String title = Jsoup.parse(itemInfo).select("#div.sku-name").text();
                item.setTitle(title);

                item.setCreate(new Date());
                item.setUpdate(item.getCreate());

                itemService.save(item);
            }
        }
    }

}
