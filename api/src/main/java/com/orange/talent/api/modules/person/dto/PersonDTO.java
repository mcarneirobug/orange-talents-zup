package com.orange.talent.api.modules.person.dto;

import com.orange.talent.api.model.Ticket;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonDTO {

    private Long id;

    @NotEmpty(message = "Favor informar o e-mail.")
    @Email
    private String email;

    private List<Ticket> tickets;

}
