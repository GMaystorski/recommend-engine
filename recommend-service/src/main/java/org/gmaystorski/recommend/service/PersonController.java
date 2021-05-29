package org.gmaystorski.recommend.service;

import java.util.List;

import org.gmaystorski.recommend.service.model.PersonDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/person")
public class PersonController {

    @Autowired
    private DBService service;

    @GetMapping(path = "")
    public List<PersonDTO> getPersons() {
        return service.getPersons();
    }
}
