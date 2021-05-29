package org.gmaystorski.recommend.service;

import org.gmaystorski.recommend.service.model.RecommendationsDTO;
import org.gmaystorski.recommend.service.model.UserInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/recommend")
public class RecommendServiceController {

    @Autowired
    private RecommendService service;

    @GetMapping(path = "")
    public RecommendationsDTO getRecommendations(@RequestBody UserInput input) {
        return service.processInput(input);
    }

}
