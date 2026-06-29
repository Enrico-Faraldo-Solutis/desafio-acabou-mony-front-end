package com.example.acabou_mony_account.repository;

import com.example.acabou_mony_account.entity.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaRepository extends JpaRepository<Conta, Long> {
}
