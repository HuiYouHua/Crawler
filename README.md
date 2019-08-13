# Crawler
java实现的爬虫技术



### Crawler-jd

抓取京东首页商品信息

数据库

```mysql
CREATE TABLE `jd_item` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `spu` bigint(15) DEFAULT NULL COMMENT '商品集合id',
  `sku` bigint(15) DEFAULT NULL COMMENT '商品最小品类单元id',
  `title` varchar(100) DEFAULT NULL COMMENT '商品标题',
  `price` bigint(10) DEFAULT NULL COMMENT '商品价格',
  `pic` varchar(200) DEFAULT NULL COMMENT '商品图片',
  `url` varchar(200) DEFAULT NULL COMMENT '商品详细地址',
  `create` datetime DEFAULT NULL COMMENT '创建时间',
  `update` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `sku` (`sku`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='京东商品表';
```



### Crawler-51job

抓取51job职位信息

数据库

```mysql
CREATE TABLE `job_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `company_name` varchar(100) DEFAULT NULL COMMENT '公司名称',
  `company_addr` varchar(200) DEFAULT NULL COMMENT '公司联系方式',
  `company_info` text COMMENT '公司信息',
  `job_name` varchar(100) DEFAULT NULL COMMENT '职位名称',
  `job_addr` varchar(50) DEFAULT NULL COMMENT '工作地点',
  `job_info` text COMMENT '职位信息',
  `salary_min` int(10) DEFAULT NULL COMMENT '薪资范围，最小',
  `salary_max` int(10) DEFAULT NULL COMMENT '薪资方位，最大',
  `url` varchar(150) DEFAULT NULL COMMENT '招聘信息详情页',
  `time` varchar(100) DEFAULT NULL COMMENT '职位最近发布时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=351 DEFAULT CHARSET=utf8 COMMENT='招聘信息';
```

![image-20190813172004285](/Users/huahuiyou/Library/Application Support/typora-user-images/image-20190813172004285.png)

