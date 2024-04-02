package com.example.demo.repositories;

import com.example.demo.models.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("select c from Category c where c.id=:categoryId")
    Category getCategoryById(@Param("categoryId") Integer categoryId);
}
