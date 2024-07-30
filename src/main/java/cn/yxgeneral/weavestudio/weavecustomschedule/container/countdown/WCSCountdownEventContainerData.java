package cn.yxgeneral.weavestudio.weavecustomschedule.container.countdown;

public class WCSCountdownEventContainerData {
    int CurrentTotalCount = 0;
    int CurrentIndex = 0;
    public int getCurrentTotalCount() {
        return CurrentTotalCount;
    }
    public void currentTotalCountRecord(){
        CurrentTotalCount++;
    }
    public void setCurrentTotalCount(int currentTotalCount) {
        CurrentTotalCount = currentTotalCount;
    }
    public void resetCurrentTotalCount(){
        CurrentTotalCount = 0;
    }
    public int getCurrentIndex() {
        return CurrentIndex;
    }
    public void currentIndexRecord(){
        CurrentIndex++;
    }
    public void setCurrentIndex(int currentIndex) {
        CurrentIndex = currentIndex;
    }
    public void resetCurrentIndex(){
        CurrentIndex = 0;
    }

}
