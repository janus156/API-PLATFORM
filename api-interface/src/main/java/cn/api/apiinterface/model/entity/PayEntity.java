package cn.api.apiinterface.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayEntity {
    @NotBlank
    Long userId;
    @NotBlank
    Long interfaceId;
}
