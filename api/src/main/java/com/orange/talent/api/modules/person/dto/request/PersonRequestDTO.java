package com.orange.talent.api.modules.person.dto.request;

import com.orange.talent.api.model.Ticket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonRequestDTO {

    private Long id;

    @NotEmpty(message = "Favor informar o e-mail.")
    @Email
    private String email;

    private List<Ticket> tickets;

}
