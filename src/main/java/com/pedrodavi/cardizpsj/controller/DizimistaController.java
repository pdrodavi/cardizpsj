package com.pedrodavi.cardizpsj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dizimista")
public class DizimistaController {

    @Autowired
    private EntityService service;

    // Listar todos
    @GetMapping
    public List<Entity> getAllEntities() {
        return service.findAll();
    }

    // Buscar por ID
    @GetMapping("/{cod}")
    public ResponseEntity<Entity> getEntityById(@PathVariable Long cod) {
        return service.findById(cod)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Criar nova entidade
    @PostMapping
    public ResponseEntity<Entity> createEntity(@RequestBody Entity entity) {
        Entity savedEntity = service.save(entity);
        return new ResponseEntity<>(savedEntity, HttpStatus.CREATED);
    }

    // Atualizar entidade
    @PutMapping("/{cod}")
    public ResponseEntity<Entity> updateEntity(@PathVariable Long cod, @RequestBody Entity entity) {
        return service.findById(cod)
                .map(existingEntity -> {
                    existingEntity.setNome(entity.getNome());
                    Entity updatedEntity = service.save(existingEntity);
                    return new ResponseEntity<>(updatedEntity, HttpStatus.OK);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Deletar entidade
    @DeleteMapping("/{cod}")
    public ResponseEntity<Void> deleteEntity(@PathVariable Long cod) {
        return service.findById(cod)
                .map(entity -> {
                    service.deleteById(cod);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
