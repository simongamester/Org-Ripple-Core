/*
 * Copyright 2016 Ripple OSI
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
package org.rippleosi.security.model;

public class Claims {

    private String homeView;
    private boolean autoAdvancedSearch;

    public String getHomeView() {
        return homeView;
    }

    public void setHomeView(String homeView) {
        this.homeView = homeView;
    }

    public boolean isAutoAdvancedSearch() {
        return autoAdvancedSearch;
    }

    public void setAutoAdvancedSearch(boolean autoAdvancedSearch) {
        this.autoAdvancedSearch = autoAdvancedSearch;
    }
}
