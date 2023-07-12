package src.models;

public class RequestData {
    public String Action;
    public String WalletName;
    public String WalletAddress;
    public String[] NodeServers;
    public Transfer Transfer = new Transfer();
    public CreateWallet CreateWallet = new CreateWallet();
    public Balance Balance = new Balance();
    public NewCoins NewCoins = new NewCoins();

    public  class CreateWallet {
        public String WalletName;
        public String WalletAddress;
        public String Password;
        public boolean IsBroadcasted;
        public String CreatedAt;
        public boolean IsVault;
        public float VaultLimit;
        public String VaultOpenDate;
        public String LimitTime;
    }

    public class NewCoins {
        public float amount;
        public String password;
    }

    public class Balance{
        public String WalletAddress;
    }


    public class Transfer {
        public String SenderAddress;
        public String ReceiverAddress;
        public float Amount;
        public boolean IsBroadcasted;
        public String BlockID;
    }
//    public float Amount;
//    public String SenderAddress;
}


