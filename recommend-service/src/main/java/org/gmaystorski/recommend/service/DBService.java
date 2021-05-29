package org.gmaystorski.recommend.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.gmaystorski.recommend.commons.cypher.query.CypherQueryBuilder;
import org.gmaystorski.recommend.commons.pool.ConnectionAcquisitionException;
import org.gmaystorski.recommend.commons.pool.ConnectionReleaseException;
import org.gmaystorski.recommend.commons.pool.SessionPool;
import org.gmaystorski.recommend.service.exception.DatabaseConnectionException;
import org.gmaystorski.recommend.service.model.CategoryDTO;
import org.gmaystorski.recommend.service.model.PersonDTO;
import org.gmaystorski.recommend.service.model.ProductionDTO;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DBService {

    @Autowired
    private SessionPool sessionPool;

    public List<CategoryDTO> getCategories() {
        return getAllFromClass("Category", CategoryDTO.class);

    }

    public List<PersonDTO> getPersons() {
        return getAllFromClass("Person", PersonDTO.class);
    }

    public List<ProductionDTO> getProductions() {
        return getAllFromClass("Production", ProductionDTO.class);
    }

    private <T> List<T> getAllFromClass(String className, Class<T> clazz) {
        String categoryQuery = new CypherQueryBuilder().match()
                                                       .withNode("v", className, Collections.emptyMap())
                                                       .returnValues("v")
                                                       .build();
        ObjectMapper mapper = new ObjectMapper();
        try {
            Session session = sessionPool.getTarget();
            List<T> entities = session.run(categoryQuery)
                                      .stream()
                                      .map(record -> record.get("v").asMap())
                                      .map(map -> mapper.convertValue(map, clazz))
                                      .collect(Collectors.toList());
            sessionPool.releaseTarget(session);
            return entities;
        } catch (ConnectionAcquisitionException | ConnectionReleaseException e) {
            throw new DatabaseConnectionException(e);
        }

    }

}
