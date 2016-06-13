/*
 *   Copyright 2015 Ripple OSI
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */
package org.rippleosi.common.service;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rippleosi.common.model.Result;
import org.rippleosi.common.model.VistaRestResponse;
import org.rippleosi.common.repo.Repository;
import org.rippleosi.common.types.RepoSourceType;
import org.rippleosi.common.types.RepoSourceTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class AbstractVistaService implements Repository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractVistaService.class);

    @Value("${repository.config.vista:1100}")
    private int priority;

    @Autowired
    private VistaRequestProxy requestProxy;

    @Override
    public RepoSourceType getSource() {
        return RepoSourceTypes.VISTA;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    protected <I, O> O findData(RequestStrategy<I, O> requestStrategy, Class expected) {

        final String uri = requestStrategy.getQueryUriComponents().toUriString();
        ResponseEntity<VistaRestResponse> response = new ResponseEntity(HttpStatus.EXPECTATION_FAILED);
        try{
            response = requestProxy.getWithoutSession(uri, VistaRestResponse.class);
        }catch(Exception e){
            LOGGER.warn("Vista server returned a 500 error response" + e.getMessage(), e);
        }

        final ObjectMapper objectMapper = new ObjectMapper();
        final List<I> data = new ArrayList<>();

        if (response.getStatusCode() != HttpStatus.OK) {
            LOGGER.warn("Vista query returned with status code " + response.getStatusCode());
        }
        else {
            for (final Result result : response.getBody().getResults()) {
                for (final Object item : result.getData().getItems()) {
                    data.add((I) objectMapper.convertValue(item, expected));
                }
            }
        }

        return requestStrategy.transform(data);
    }
}
