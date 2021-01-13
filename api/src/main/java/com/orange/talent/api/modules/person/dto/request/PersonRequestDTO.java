package com.orange.talent.api.modules.person.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonRequestDTO {

    @ApiModelProperty(value = "E-mail da pessoa.", required = true)
    @NotEmpty(message = "Favor informar o e-mail.")
    @Email
    private String email;

}
