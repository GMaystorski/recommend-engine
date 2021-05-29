package org.gmaystorski.recommend.service;

import java.util.List;

import org.gmaystorski.recommend.service.model.CategoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/category")
public class CategoryController {

    @Autowired
    private DBService service;

    @GetMapping(path = "")
    public List<CategoryDTO> getCategories() {
        return service.getCategories();
    }

}
