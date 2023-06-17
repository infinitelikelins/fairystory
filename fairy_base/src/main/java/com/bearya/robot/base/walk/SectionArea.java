package com.bearya.robot.base.walk;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yexifeng on 2019/3/5.
 */

public class SectionArea implements ISection{
    private List<ISection> sections = new ArrayList<>();
    private int start;

    public SectionArea(int maxColumn,int startOid,int column,int row){
        start = startOid;
        int so = startOid;
        for(int i=0;i<row;i++){
            Section section = new Section(so,so+column-1);
            sections.add(section);
            so+=maxColumn;
        }
    }

    @Override
    public boolean in(int value) {
        for(ISection section:sections){
            if(section.in(value)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int getStart() {
        return start;
    }

}
