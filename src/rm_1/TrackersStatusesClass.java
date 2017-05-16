/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rm_1;

import java.io.Serializable;

/**
 *
 * @author Саня
 */
public class TrackersStatusesClass implements Serializable{
    private String trackers;
    private String statuses;

    public String getTrackers() {
        return trackers;
    }

    public void setTrackers(String trackers) {
        this.trackers = trackers;
    }

    public String getStatuses() {
        return statuses;
    }

    public void setStatuses(String statuses) {
        this.statuses = statuses;
    }

    public TrackersStatusesClass(String trackers, String statuses) {
        this.trackers = trackers;
        this.statuses = statuses;
    }
    
}
