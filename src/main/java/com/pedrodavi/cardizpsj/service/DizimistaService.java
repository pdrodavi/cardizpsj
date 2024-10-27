package com.pedrodavi.cardizpsj.service;

import com.pedrodavi.cardizpsj.entity.Dizimista;
import com.pedrodavi.cardizpsj.repository.DizimistaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DizimistaService {

    @Autowired
    private DizimistaRepository repository;

    public List<Dizimista> findAll() {
        return repository.findAll();
    }

    public Optional<Dizimista> findById(Long cod) {
        return repository.findById(cod);
    }

    public Dizimista save(Dizimista entity) {
        return repository.save(entity);
    }

    public void deleteById(Long cod) {
        repository.deleteById(cod);
    }

}
