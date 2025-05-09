package com.pharmacy.Management.repository;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.pharmacy.Management.models.Supplier;

public interface SupplierRepository extends JpaRepositoryImplementation<Supplier, Integer> {

}
