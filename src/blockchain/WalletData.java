package src.blockchain;

import java.io.Serializable;

public class WalletData implements Serializable {
    public String Address;
    public String CreatedAt;
    public String Name;
    // is the wallet of a vault type
    public boolean Vault;
    // the limit amount. eg. 200dau per day
    public float VaultLimit;
    // date the vault is set to be open
    public String VaultOpenDate;
    // limit time  // d = day, m = month, y = year
    public String LimitTime;
}