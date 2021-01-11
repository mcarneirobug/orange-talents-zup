package com.orange.talent.api.modules.person.dto.response;

import com.orange.talent.api.model.Ticket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonResponseDTO {

    private List<Ticket> tickets;

}
