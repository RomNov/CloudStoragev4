import java.io.Serializable;

public class AuthAnswerMessage extends AbstractMessage{
    private String nickName;

    public AuthAnswerMessage(String nickName) {
        this.name = "authanswer";
        this.nickName = nickName;

    }

    public String getNickName() {
        return nickName;
    }

}
