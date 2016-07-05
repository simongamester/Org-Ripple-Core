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
package org.rippleosi.config.broker;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.CamelContext;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 */
@Configuration
@ComponentScan("com.rippleosi.common.routes")
public class BrokerConfig extends CamelConfiguration {

    @Value("${queue.broker.directory:activemq-data}")
    private String dataDirectory;

    @Bean
    public BrokerService brokerService() throws Exception {
        BrokerService brokerService = new BrokerService();
        brokerService.setBrokerName("embedded");
        brokerService.addConnector("vm://embedded");
        brokerService.setDataDirectory(dataDirectory);
        return brokerService;
    }

    @Override
    public CamelContext camelContext() throws Exception {
        brokerService().start();
        return super.camelContext();
    }

    @Bean(name = "activemq")
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("vm://embedded?create=false");
        activeMQConnectionFactory.setTrustAllPackages(true);

        return activeMQConnectionFactory;
    }
}
