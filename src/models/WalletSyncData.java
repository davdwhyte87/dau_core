package src.models;

import src.blockchain.Consencus;
//import blockchain.WalletData;
import src.blockchain.WalletData;

public class WalletSyncData {
    public src.blockchain.WalletData WalletData;
    public String WalletAddress;
    @Override public boolean equals(Object obj)
    {

        // checking if the two objects
        // pointing to same object
        if (this == obj)
            return true;

        // checking for two condition:
        // 1) object is pointing to null
        // 2) if the objects belong to
        // same class or not
        if (obj == null
                || this.getClass() != obj.getClass())
            return false;

        WalletSyncData v1 = (WalletSyncData) obj; // type casting object to the
        // intended class type

        // checking if the two
        // objects share all the same values
        return this.WalletAddress .equals(v1.WalletAddress);
    }

}
