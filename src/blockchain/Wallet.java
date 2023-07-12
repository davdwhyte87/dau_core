package src.blockchain;


import src.models.CreateWalletReq;
import src.models.RequestData;
import src.models.WalletSyncData;
import src.utils.FileUtils;
import src.utils.SecUtils;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

import kotlin.NumbersKt;


public class Wallet {
    public String Address;
    public String CreatedAt;
    public String Name;
    FileUtils fileUtils = new FileUtils();
    SecUtils secUtils = new SecUtils();

    public String CreateWallet(RequestData.CreateWallet walletReq){
        String walletAddress = "";
//        String walletName = "";
        String tempName = "";

        // gnenrating wallet address
        Random r = new Random();
        for (var i=0;i< 32; i++ ){
            char randomChar = (char) (97 + r.nextInt(16));
            tempName = tempName+ randomChar;
        }

        // if the client application (mobile app eg.) already generated an address, then there is no need to generate one
        if (walletReq.WalletAddress == null){
            // we get the wallet address by hashing the password 2times
            walletAddress = secUtils.GenerateHash(walletReq.Password+walletReq.WalletName);
        } else {
            walletAddress = walletReq.WalletAddress;
        }
       
   
        if (IsWalletExists(walletAddress)){
            System.out.println("Wallet exists ");
            return null;
        }

        // create a folder in data
        File dir = new File("data/"+walletAddress+"/");
        dir.mkdirs();

        WalletData walletData = new WalletData();
        walletData.Address = walletAddress;
        walletData.Name = walletReq.WalletName;
        if (walletReq.IsVault){
            walletData.Vault = true;
            walletData.VaultLimit = walletReq.VaultLimit;
            walletData.VaultOpenDate = walletReq.VaultOpenDate;
            walletData.LimitTime = walletReq.LimitTime;
        }



        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        walletData.CreatedAt =formatter.format(date);

        fileUtils.CreateFileObject(walletData, "data/"+walletAddress+"/data.bin");

        // create new blockchain data
        Blockchain blockchain = new Blockchain();
        blockchain.NewChain(walletAddress, walletReq.Password);


//        WalletData walletx = (WalletData) obj;
//        System.out.println("Read wallet address :"+walletx.Address);
//        System.out.println("Read wallet name :"+walletx.Name);
//        System.out.println("Read wallet time :"+walletx.CreatedAt);


        // broadcast wallet creation
        RequestData requestData = new RequestData();
        BroadCast broadCast = new BroadCast();
        requestData.Action = "CREATE_WALLET";
        requestData.CreateWallet.IsBroadcasted = true;
        requestData.CreateWallet.WalletAddress = walletAddress;
        requestData.CreateWallet.WalletName = walletReq.WalletName;
        requestData.CreateWallet.Password = walletReq.Password;
        broadCast.BCreateWallet(requestData);


        System.out.println("returning wallet address");
        return walletAddress;
    }
    public boolean IsWalletExists(String WalletName){
        File f = new File("data/"+WalletName+"/");
        if (f.isDirectory()){
            return true;
        }
        return false;
    }

    public WalletSyncData[] GetWallets(){
        File directoryPath = new File("data/");
        String[] walletAddresses = directoryPath.list();
        FileUtils fileUtils = new FileUtils();
        ArrayList<WalletSyncData> walletSyncDatalist= new ArrayList<WalletSyncData>();
        assert walletAddresses != null;
        for (String address: walletAddresses){
           Object wallObj = fileUtils.ReadFileObj(address);
           WalletData walletData = (WalletData) wallObj;
           WalletSyncData walletSyncData = new WalletSyncData();
           walletSyncData.WalletAddress = address;
           walletSyncData.WalletData = walletData;
           walletSyncDatalist.add(walletSyncData);
        }

        return walletSyncDatalist.toArray(new WalletSyncData[0]);

    }

    public void CreateMultipleWallets(WalletSyncData[] wallets){
       FileUtils fileUtils = new FileUtils();
       for(WalletSyncData syncData: wallets) {
           // if wallet address does not exist
           if(!IsWalletExists(syncData.WalletAddress)){
               fileUtils.CreateFileObject(syncData.WalletData, syncData.WalletAddress);
           }

       }

    }


    public void IssueNewCoins(float amount, String password){
        // this allows users to issue new tokens
        String WalletAddress = "new_issued";
        WalletData walletData = new WalletData();
        walletData.Address = WalletAddress;
        walletData.Name = "New Mint";

        // create a folder in data
        File dir = new File("data/"+WalletAddress+"/");
        dir.mkdirs();

        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        walletData.CreatedAt =formatter.format(date);

        fileUtils.CreateFileObject(walletData, "data/"+WalletAddress+"/data.bin");

        // create new blockchain data
        Blockchain blockchain = new Blockchain();
        blockchain.Mint(amount, password, WalletAddress);
    }
}


