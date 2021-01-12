package com.orange.talent.api.modules.person.dto.request;

import com.orange.talent.api.model.Ticket;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonRequestDTO {

    @ApiModelProperty(value = "E-mail da pessoa.", required = true)
    @NotEmpty(message = "Favor informar o e-mail.")
    @Email
    private String email;

}
