package com.align.shophub.repository;

import com.align.shophub.entity.Cart;
import com.align.shophub.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeatureRepository extends JpaRepository<Feature, Long> {

}
