package com.bipros.pegamunda.entity;

import javax.persistence.*;

/**
 * This class is the parent of all which will help me creating the repos for dynamic entities
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    String firstName;

    public String getFirstName() {
        return firstName;
    }
}
