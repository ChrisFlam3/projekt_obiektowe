package utils;

public class Vector2 {
    public int x=0;
    public int y=0;

    public Vector2(int x,int y){
        this.x=x;
        this.y=y;
    }

   public Vector2 sum(Vector2 other){
        return new Vector2(this.x+other.x,this.y+other.y);
    }

    public Vector2 substract(Vector2 other){
        return new Vector2(this.x-other.x,this.y-other.y);
    }

    public Vector2 multiply(Vector2 other){
        return new Vector2(this.x*other.x,this.y*other.y);
    }

    public Vector2 div(Vector2 other){
        return new Vector2(this.x/other.x,this.y/other.y);
    }

    @Override
    public boolean equals(Object other){
        if (this == other)
            return true;
        if (!(other instanceof Vector2))
            return false;
        Vector2 that = (Vector2) other;
        return this.x==that.x&&this.y==that.y;
    }


    public Vector2 opposite(){
        return new Vector2(-this.x,-this.y);
    }

    @Override
    public int hashCode() {
        int hash = 13;
        hash += this.x * 31;
        hash += this.y * 17;
        return hash;
    }

}
