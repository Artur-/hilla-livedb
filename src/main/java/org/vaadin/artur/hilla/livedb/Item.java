package org.vaadin.artur.hilla.livedb;

import java.time.LocalDateTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import dev.hilla.Nonnull;

@Table
public class Item {

    @Id
    private Integer id;

    @Nonnull
    @NotBlank
    @Max(100)
    private String name;

    private int quantity;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public Item() {

    }

    public Item(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
