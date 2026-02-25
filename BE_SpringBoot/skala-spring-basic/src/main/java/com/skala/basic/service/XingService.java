package com.skala.basic.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skala.basic.data.XingEntity;
import com.skala.basic.repository.XingRepository;

@Service
public class XingService {

    private final XingRepository xingRepository = new XingRepository();

    public List<XingEntity> getXingList() {
        return xingRepository.findAll();
    }

    public XingEntity getXingById(Long id) {
        return xingRepository.findById(id);
    }

    public List<XingEntity> getXingsByNames(String[] names) {
        return xingRepository.findByNames(names);
    }
}
