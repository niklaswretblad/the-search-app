
package com.example.thesearch.model;

public class SpotRequest {
    private Spot spot;

    public SpotRequest(Spot spot) {
        this.spot = spot;
    }

    public Spot getSpot() {
        return spot;
    }

    public void setSpot(Spot spot) {
        this.spot = spot;
    }
}
