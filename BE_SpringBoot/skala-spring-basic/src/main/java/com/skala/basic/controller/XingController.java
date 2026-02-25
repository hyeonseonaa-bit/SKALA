package com.skala.basic.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skala.basic.data.XingEntity;
import com.skala.basic.service.XingService;

@RestController
@RequestMapping("/xings")
public class XingController {

    @Autowired
    private XingService xingService;

    @GetMapping
    public List<XingEntity> getXingList() {
        return xingService.getXingList();
    }

    @GetMapping("/{id}")
    public XingEntity getXingById(@PathVariable Long id) {
        return xingService.getXingById(id);
    }

    @GetMapping("/search")
    public List<XingEntity> getXingsByNames(@RequestParam String[] names) {
        return xingService.getXingsByNames(names);
    }
}
