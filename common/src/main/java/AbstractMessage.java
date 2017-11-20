import java.io.Serializable;

abstract class AbstractMessage implements Serializable{
    protected String name;

    public String getName() {
        return name;
    }
}
