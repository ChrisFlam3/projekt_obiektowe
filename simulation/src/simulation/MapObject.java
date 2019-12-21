package simulation;


import utils.Vector2;

public class MapObject {
    public Vector2 pos;
    IStateObserver observer;

public MapObject(Vector2 pos){
    this.pos=pos;
}
    public void addObserver(IStateObserver observer){
        this.observer=observer;
    }

}
