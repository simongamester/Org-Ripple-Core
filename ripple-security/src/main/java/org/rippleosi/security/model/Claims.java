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
