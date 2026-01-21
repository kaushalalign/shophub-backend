package com.align.shophub.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity

@Data
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // e.g. banner image URL
    private String image;
}