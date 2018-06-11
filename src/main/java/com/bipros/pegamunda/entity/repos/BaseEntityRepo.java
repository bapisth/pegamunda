package com.bipros.pegamunda.entity.repos;

import com.bipros.pegamunda.entity.BaseEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseEntityRepo extends CrudRepository<BaseEntity, Long> {
}
