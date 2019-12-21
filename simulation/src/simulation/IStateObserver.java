package simulation;

import utils.Vector2;

public interface IStateObserver {
    void positionChanged(Vector2 oldPosition, MapObject obj);
    void notPresent(MapObject obj);
}
