/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rm_1;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Саня
 */
public class TrackersStatusesClass implements Serializable{

    private List<String> trackers;
    private List<String> statuses;
    private List<String> names;
    private String project;
    private String subproject;

    public String getSubproject() {
        return subproject;
    }

    public void setSubproject(String subproject) {
        this.subproject = subproject;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }
    
    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public TrackersStatusesClass(List<String> trackers, List<String> statuses, List<String> names, String project, String subproject) {
        this.trackers = trackers;
        this.statuses = statuses;
        this.names = names;
        this.subproject = subproject;
        this.project = project;
    }
    
    public List<String> getTrackers() {
        return trackers;
    }

    public void setTrackers(List<String> trackers) {
        this.trackers = trackers;
    }

    public List<String> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<String> statuses) {
        this.statuses = statuses;
    }

    public String getTrackerByIndex(int i){
        if(trackers.size()>i)
            return trackers.get(i);
        else
            return "";
    }
    
    public String getStatusByIndex(int i){
        if(statuses.size()>i)
            return statuses.get(i);
        else
            return "";
    }
    
    public void setTrackerByIndex(String s, int i)
    {
        if(i==-1)
            trackers.add(s);
        else
            trackers.set(i, s);
    }
    
    public void setStatusByIndex(String s, int i)
    {
        if(i==-1)
            statuses.add(s);
        else
            statuses.set(i, s);
    }
    
    public String getNameByIndex(int i){
        if(names.size()>i)
            return names.get(i);
        else
            return "";
    }
    
    public void setNameByIndex(String s, int i)
    {
        if(i==-1)
            names.add(s);
        else
            names.set(i, s);
    }
}
