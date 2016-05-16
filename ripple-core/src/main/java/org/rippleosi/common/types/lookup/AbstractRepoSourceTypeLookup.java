/*
 * Copyright 2015 Ripple OSI
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.rippleosi.common.types.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import org.rippleosi.common.types.RepoSourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 */
public abstract class AbstractRepoSourceTypeLookup<R extends RepoSource> implements RepoSourceFactory<R> {

    private final List<R> repoSources = new ArrayList<>();
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RepoSourceType.class);

    @Autowired
    private ApplicationContext applicationContext;

    protected abstract R defaultSourceLookup();
    protected abstract Class<R> repoSourceClass();

    @Override
    public RepoSourceType lookup(final String sourceName) {
        
        RepoSourceType sourceType = null;
        for (R repoSource : repoSources) {
            sourceType = repoSource.getSource(sourceName);
            if (null != sourceType) {
                return sourceType;
            }
        }

        sourceType = defaultSourceLookup().getSource(sourceName);
        if (null == sourceType) {
            LOGGER.warn("Could not find an enumeration for '" + sourceName +"'");
        }
        return sourceType;
    }
    
    @PostConstruct
    public void postConstruct() {
        Map<String, R> beans = applicationContext.getBeansOfType(repoSourceClass());

        for (R repoSourceTypeLookup : beans.values()) {
            repoSources.add(repoSourceTypeLookup);
        }
    }
}
