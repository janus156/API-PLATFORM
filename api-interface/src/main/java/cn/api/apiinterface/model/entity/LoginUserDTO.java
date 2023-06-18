package cn.api.apiinterface.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginUserDTO implements Serializable{
    UserDTO user;

    private String[] permissions;

}
