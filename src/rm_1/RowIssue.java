/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rm_1;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Саня
 */
public class RowIssue {
        private String idCol;
        private String statusCol;
        private String priorityCol;
        private String themeCol;
        private String spentHoursCol;

    public void setStatusCol(String statusCol) {
        this.statusCol = statusCol;
    }
        private String priorityIdCol;

    public void setIdCol(String idCol) {
        this.idCol = idCol;
    }

    public void setPriorityCol(String priorityCol) {
        this.priorityCol = priorityCol;
    }

    public void setThemeCol(String themeCol) {
        this.themeCol = themeCol;
    }

    public void setSpentHoursCol(String spentHoursCol) {
        this.spentHoursCol = spentHoursCol;
    }

    public void setPriorityIdCol(String priorityIdCol) {
        this.priorityIdCol = priorityIdCol;
    }
    public String getStatusCol() {
        return statusCol;
    }

    public String getPriorityCol() {
        return priorityCol;
    }

    public String getThemeCol() {
        return themeCol;
    }

    public String getSpentHoursCol() {
        return spentHoursCol;
    }

 public String getIdCol() {
        return idCol;
    }
   public String getPriorityIdCol() {
        return priorityIdCol;
    }
   
    
        public RowIssue(String iden, String stat, String prio, String them, String spent, String prioId) {
            this.idCol=iden;
            this.statusCol = stat;
            this.priorityCol = prio;
            this.themeCol = them;
            this.spentHoursCol = spent;
            this.priorityIdCol=prioId;
        }

}
