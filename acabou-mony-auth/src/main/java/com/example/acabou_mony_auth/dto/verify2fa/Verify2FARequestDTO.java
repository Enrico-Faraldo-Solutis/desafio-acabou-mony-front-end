package com.example.acabou_mony_auth.dto.verify2fa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Verify2FARequestDTO {
    private Long usuarioId;
    private String codigo;
}