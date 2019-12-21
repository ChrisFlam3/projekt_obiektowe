package utils;

public class Direction {
    static final Vector2[] directions={new Vector2(0,1),new Vector2(1,1),new Vector2(1,0),new Vector2(1,-1),
            new Vector2(0,-1),new Vector2(-1,-1),new Vector2(-1,0),new Vector2(-1,1)};

    int orientation=0;

    public Direction(int orientation){
        this.orientation=orientation;
    }

    public void rotate(int rot){
        this.orientation=(this.orientation+rot)%8;
        if(this.orientation<0)
            this.orientation=this.orientation*-1;
    }

    public Vector2 getDirVec(){
        return this.directions[orientation];
    }

}
