package com.huayoyu.jd.service.impl;

import com.huayoyu.jd.dao.ItemDao;
import com.huayoyu.jd.pojo.Item;
import com.huayoyu.jd.service.ItemService;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private ItemDao itemDao;

    @Override
    @Transactional
    public void save(Item item) {
        itemDao.save(item);
    }

    @Override
    public List<Item> findAll(Item item) {
        Example<Item> example = Example.of(item);

        List<Item> itemList = itemDao.findAll(example);
        return itemList;
    }
}
