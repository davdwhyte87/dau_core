package src.blockchain;

import java.io.Serializable;

public class BlockData implements Serializable {
    public String ID;
    public String Hash;
    public String PrevHash;
    public float Amount;
    public String Sender;
    public String Receiver;
    public float Balance;
    public String CreatedAt;
    public String PrivateKeyHash;
}
