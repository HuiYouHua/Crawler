package com.huayoyu.webmagic.service;

import com.huayoyu.webmagic.mapper.JobInfoMapper;
import com.huayoyu.webmagic.pojo.JobInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobInfoService {

    @Autowired
    private JobInfoMapper jobInfoMapper;


    @Transactional
    public void save(JobInfo jobInfo) {
        //根据url和发布时间查询数据
        JobInfo param = new JobInfo();
        param.setUrl(jobInfo.getUrl());
        param.setTime(jobInfo.getTime());

        //执行查询
        List<JobInfo> list = this.findJobInfo(param);

        //判断查询结果是否为空
        if (list.size() == 0) {
            //如果查询结果为空，表示招聘信息数据不存在，或者已经更新了，需要新增或者更新数据库
            jobInfoMapper.insertSelective(jobInfo);
        }
    }

    public List<JobInfo> findJobInfo(JobInfo jobInfo) {

        //设置查询条件

        //执行查询
        List list = jobInfoMapper.select(jobInfo);

        return list;
    }

}
