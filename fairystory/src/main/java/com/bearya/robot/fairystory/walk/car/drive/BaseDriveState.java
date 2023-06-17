package com.bearya.robot.fairystory.walk.car.drive;


public abstract class BaseDriveState implements IState {
    private boolean perform;

    @Override
    public void makeFaceStartLoad() {

    }

    @Override
    public void recognitionLoad() {

    }

    @Override
    public void newLoad() {

    }


    @Override
    public void computeExitPath() {

    }

    @Override
    public void unLocking() {

    }

    @Override
    public void travel() {

    }

    @Override
    public void arriveTarget() {

    }

    @Override
    public void exitLoad() {

    }

    @Override
    public void inObstacle() {

    }

    @Override
    public void outOfLoad() {

    }

    @Override
    public void makeFaceEndLoad() {

    }

    public void reset(){
        perform = false;
    }

    protected void doPerform(){
        perform = true;
    }

    protected boolean isPerform(){
        return perform;
    }

}