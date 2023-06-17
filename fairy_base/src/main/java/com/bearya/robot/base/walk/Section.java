package com.bearya.robot.base.walk;

public class Section implements ISection {
    /**
     * 开始值(包含)
     *
     */
    private final int start;
    /**
     * 结束值(包含)
     */
    private final int end;

    public Section(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public boolean in(int value){
        return value>=start && value<=end;
    }
}
