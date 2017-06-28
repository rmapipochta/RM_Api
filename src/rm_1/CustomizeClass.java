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
public class CustomizeClass implements Serializable{
    private int ID_STATUS_IN_WORK;
    private int ID_STATUS_NEW;
    private int ID_STATUS_FINISHED;
    private int ID_STATUS_DEFER;
    private int ID_STATUS_VALUATION_SPENT_TIME;
    private int ID_ACTIVITY_DEVELOPMENT;
    private int ID_ACTIVITY_ENGINEERING;

    public int getID_STATUS_IN_WORK() {
        return ID_STATUS_IN_WORK;
    }

    public void setID_STATUS_IN_WORK(int ID_STATUS_IN_WORK) {
        this.ID_STATUS_IN_WORK = ID_STATUS_IN_WORK;
    }

    public int getID_STATUS_NEW() {
        return ID_STATUS_NEW;
    }

    public void setID_STATUS_NEW(int ID_STATUS_NEW) {
        this.ID_STATUS_NEW = ID_STATUS_NEW;
    }

    public int getID_STATUS_FINISHED() {
        return ID_STATUS_FINISHED;
    }

    public void setID_STATUS_FINISHED(int ID_STATUS_FINISHED) {
        this.ID_STATUS_FINISHED = ID_STATUS_FINISHED;
    }

    public int getID_STATUS_DEFER() {
        return ID_STATUS_DEFER;
    }

    public void setID_STATUS_DEFER(int ID_STATUS_DEFER) {
        this.ID_STATUS_DEFER = ID_STATUS_DEFER;
    }

    public int getID_STATUS_VALUATION_SPENT_TIME() {
        return ID_STATUS_VALUATION_SPENT_TIME;
    }

    public void setID_STATUS_VALUATION_SPENT_TIME(int ID_STATUS_VALUATION_SPENT_TIME) {
        this.ID_STATUS_VALUATION_SPENT_TIME = ID_STATUS_VALUATION_SPENT_TIME;
    }

    public int getID_ACTIVITY_DEVELOPMENT() {
        return ID_ACTIVITY_DEVELOPMENT;
    }

    public void setID_ACTIVITY_DEVELOPMENT(int ID_ACTIVITY_DEVELOPMENT) {
        this.ID_ACTIVITY_DEVELOPMENT = ID_ACTIVITY_DEVELOPMENT;
    }

    public int getID_ACTIVITY_ENGINEERING() {
        return ID_ACTIVITY_ENGINEERING;
    }

    public void setID_ACTIVITY_ENGINEERING(int ID_ACTIVITY_ENGINEERING) {
        this.ID_ACTIVITY_ENGINEERING = ID_ACTIVITY_ENGINEERING;
    }

    public CustomizeClass(int ID_STATUS_IN_WORK, int ID_STATUS_NEW, int ID_STATUS_FINISHED, int ID_STATUS_DEFER, int ID_STATUS_VALUATION_SPENT_TIME, int ID_ACTIVITY_DEVELOPMENT, int ID_ACTIVITY_ENGINEERING) {
        this.ID_STATUS_IN_WORK = ID_STATUS_IN_WORK;
        this.ID_STATUS_NEW = ID_STATUS_NEW;
        this.ID_STATUS_FINISHED = ID_STATUS_FINISHED;
        this.ID_STATUS_DEFER = ID_STATUS_DEFER;
        this.ID_STATUS_VALUATION_SPENT_TIME = ID_STATUS_VALUATION_SPENT_TIME;
        this.ID_ACTIVITY_DEVELOPMENT = ID_ACTIVITY_DEVELOPMENT;
        this.ID_ACTIVITY_ENGINEERING = ID_ACTIVITY_ENGINEERING;
    }
    
    public boolean isOk(){
        return (ID_STATUS_IN_WORK>0&&ID_STATUS_NEW>0&&ID_STATUS_FINISHED>0&&
    ID_STATUS_DEFER>0&&ID_STATUS_VALUATION_SPENT_TIME>0&&
    ID_ACTIVITY_DEVELOPMENT>0&&ID_ACTIVITY_ENGINEERING>0);
    }
}
