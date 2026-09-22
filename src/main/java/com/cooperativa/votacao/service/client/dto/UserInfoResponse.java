package com.cooperativa.votacao.service.client.dto;

import com.cooperativa.votacao.model.enums.StatusAssociadoVoto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoResponse {
    private StatusAssociadoVoto status;
}
