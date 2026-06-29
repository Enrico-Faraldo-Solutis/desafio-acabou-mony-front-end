package com.example.acabou_mony_auth.repository;

import com.example.acabou_mony_auth.entity.Codigo2FA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Codigo2FARepository extends JpaRepository<Codigo2FA, Long> {

    Optional<Codigo2FA> findFirstByUsuarioIdAndUtilizadoFalseOrderByIdDesc(Long usuarioId);

    List<Codigo2FA> findByUsuarioIdAndUtilizadoFalse(Long usuarioId);
}