package src.models;

public class CreateWalletReq {
    public String WalletName;
    public String WalletAddress;
    public boolean WalletVault;
    public String Password;
    public float VaultLimit;
    public String VaultOpenDate;
    // d = day, m = month, y = year
    // d == 100dau per day limit
    public String LimitTime;
}

