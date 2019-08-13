package com.huayoyu.webmagic.task;

import com.huayoyu.webmagic.pojo.JobInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

@Component
public class JobProcessor implements PageProcessor {

    @Autowired
    private SpringDataPipeline pipeline;

    private String url = "https://search.51job.com/list/000000,000000,0000,00,9,99,java,2,1.html?lang=c&stype=&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99&providesalary=99&lonlat=0%2C0" +
            "&radius=-1&ord_field=0&confirmdate=9&fromType=&dibiaoid=0&address=&line=&specialarea=00&from=&welfare=";

    @Override
    public void process(Page page) {
        List<Selectable> list = page.getHtml().css("div#resultList div.el").nodes();

        if (list.size() == 0) {
            // 详情页
            saveJobInfo(page);
        } else {
            // 列表页
            for (Selectable selectable : list) {
                String jobInfoUrl = selectable.links().toString();

                page.addTargetRequest(jobInfoUrl);

            }

            // 获取下一页的url
            String bkUrl = page.getHtml().css("div.p_in li.bk").nodes().get(1).links().toString();
            page.addTargetRequest(bkUrl);

        }


    }

    private void saveJobInfo(Page page) {
        JobInfo jobInfo = new JobInfo();
        //解析页面
        Html html = page.getHtml();

        //获取发布信息
        String content = Jsoup.parse(html.css("div.cn p").regex(".*发布").toString()).text();

        //获取数据，封装到对象中
        jobInfo.setCompanyName(html.css("div.cn p.cname a","text").toString());
        jobInfo.setCompanyAddr(Jsoup.parse(html.css("div.bmsg").nodes().get(1).toString()).text());
        jobInfo.setCompanyInfo(Jsoup.parse(html.css("div.tmsg").toString()).text());
        jobInfo.setJobName(html.css("div.cn h1","text").toString());
        jobInfo.setJobAddr(content.substring(0,2));
        jobInfo.setJobInfo(Jsoup.parse(html.css("div.job_msg").toString()).text());
        jobInfo.setTime(content.substring(content.length()-7,content.length()-2));
        jobInfo.setUrl(page.getUrl().toString());

        //获取薪资
        Integer[] salary = MathSalary.getSalary(html.css("div.cn strong", "text").toString());
        jobInfo.setSalaryMin(salary[0]);
        jobInfo.setSalaryMax(salary[1]);


        //把结果保存起来
        page.putField("jobInfo",jobInfo);

    }

    private Site site = new Site()
            .setCharset("gbk")
            .setTimeOut(10*1000)
            .setRetrySleepTime(3000)
            .setRetryTimes(3);

    @Override
    public Site getSite() {
        return site;
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 100000)
    public void process() {
        Spider.create(new JobProcessor())
                .addUrl(url)
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000)))
                .thread(10)
                .addPipeline(pipeline)
                .run();
    }
}
