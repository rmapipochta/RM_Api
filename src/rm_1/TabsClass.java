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
public class TabsClass implements Serializable{
    private List<TrackersStatusesClass> tsc;
    private List<String> text;
    private int size;
    
    public List<TrackersStatusesClass> getTsc() {
        return tsc;
    }

    public void setTsc(List<TrackersStatusesClass> tsc) {
        this.tsc = tsc;
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }
   
    public void addNewTab(TrackersStatusesClass ts, String txt)
    {
        tsc.add(ts);
        text.add(txt);
        size++;
    }

    public TrackersStatusesClass getTSCByIndex(int i)
    {
        return tsc.get(i);
    }
    
    public String getTextByIndex(int i)
    {
        return text.get(i);
    }
    public void setTSCByIndex(TrackersStatusesClass ts, int i)
    {
        tsc.set(i, ts);
    }
    
    public void setTextByIndex(String txt, int i)
    {
        text.set(i, txt);
    }
    public TabsClass(List<TrackersStatusesClass> tsc, List<String> text) {
        this.tsc = tsc;
        this.text = text;
        size=tsc.size();
    }
   
}
