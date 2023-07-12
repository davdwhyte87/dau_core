package src.blockchain;


import src.models.RequestData;
import src.models.ResponseData;
import org.jetbrains.annotations.Nullable;
import src.utils.CommonUtils;
import src.utils.FileUtils;
import src.utils.NetUtils;
import src.utils.SecUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Blockchain {
    SecUtils secUtils = new SecUtils();
    CommonUtils commonUtils = new CommonUtils();
    public ArrayList<BlockData> blockchain;
    FileUtils fileUtils = new FileUtils();
    // this creates a new blocklist for a new wallet
    public void NewChain(String address, String password){
        blockchain = new ArrayList<>();
        FileUtils fileUtils = new FileUtils();
        // generate new block
        BlockData blockData = NewGenesisBlock(address, password);
        // add block to chain
        blockchain.add(blockData);
        fileUtils.CreateFileObject(blockchain, "data/"+address+"/chain.bin");
    }

    // this creates a new block in the chain
    public BlockData NewGenesisBlock(String address, String password){
        BlockData blockData = new BlockData();
        blockData.Amount = 100;
        blockData.Balance = 100;
        blockData.ID = secUtils.GeneratePrivateKey(secUtils.GenerateRandString());
        blockData.Sender = "0000000";
        blockData.Receiver = address;
        blockData.CreatedAt = commonUtils.CurrentDate();
        blockData.PrevHash = "0000000";
        blockData.PrivateKeyHash = secUtils.GenerateHash(password);
        blockData.Hash = secUtils.GenerateHash(GetBlockHString(blockData,address));
        return blockData;
    }


    public void Mint(float amount, String password, String address){
        blockchain = new ArrayList<>();
        FileUtils fileUtils = new FileUtils();
        // generate new block
        BlockData blockData = new BlockData();
        blockData.Amount = amount;
        blockData.Balance = amount;
        blockData.ID = secUtils.GeneratePrivateKey(secUtils.GenerateRandString());
        blockData.Sender = "0000000";
        blockData.Receiver = address;
        blockData.CreatedAt = commonUtils.CurrentDate();
        blockData.PrevHash = "0000000";
        blockData.PrivateKeyHash = secUtils.GenerateHash(password);
        blockData.Hash = secUtils.GenerateHash(GetBlockHString(blockData,address));

        // add block to chain
        blockchain.add(blockData);
        fileUtils.CreateFileObject(blockchain, "data/"+address+"/chain.bin");
    }

    public String GetBlockHString(BlockData block, String address){
        return address+block.Sender+block.Receiver+block.PrevHash;
    }

    public boolean AddBlock(BlockData block, String address){
        ArrayList<BlockData> blocks = new ArrayList<BlockData>(List.of(GetChain(address))) ;
        blocks.add(block);
        boolean ok =fileUtils.CreateFileObject(blocks, "data/"+address+"/chain.bin");
        return ok;
    }

    public BlockData[] GetChain(String address) {
        Object obj =fileUtils.ReadFileObj("data/"+address+"/chain.bin");
        ArrayList<BlockData> bkdata = (ArrayList<BlockData>) obj;
        return bkdata.toArray(new BlockData[0]);
    }

    public boolean IsBlockExists(String address, String BlockID){
        BlockData[] blocks = GetChain(address);
        for (BlockData block: blocks){
            if (Objects.equals(block.ID, BlockID)){
                return true;
            }
        }
        return false;
    }


    public BlockData GetLastBlock(String address) {
        Object obj =fileUtils.ReadFileObj("data/"+address+"/chain.bin");
        ArrayList<BlockData> bkdata = new ArrayList<BlockData>();
        bkdata = (ArrayList) obj;
        BlockData[] blocks = bkdata.toArray(new BlockData[0]);
        BlockData lastBlock = blocks[blocks.length-1];
//        System.out.println(lastBlock.Hash);
//        System.out.println(lastBlock.Reciever);
        return lastBlock;
    }

    public boolean VerifyLastBlock(String address){
        Object obj =fileUtils.ReadFileObj("data/"+address+"/chain.bin");
        ArrayList<BlockData> bkdata = new ArrayList<BlockData>();
        bkdata = (ArrayList) obj;
        BlockData[] blocks = bkdata.toArray(new BlockData[0]);
        BlockData lastBlock = blocks[blocks.length-1];
        System.out.println(lastBlock.Hash);
        System.out.println(lastBlock.Receiver);

        if (blocks.length > 1){
            BlockData prevBlockData =  blocks[blocks.length-2];
            String hash = secUtils.GenerateHash(GetBlockHString(prevBlockData, address));

            if (lastBlock.PrevHash == hash ){
                return true;
            }else {
                return false;
            }
        }

        if (blocks.length == 1){
//            System.out.println("One block found ");
            String hash = secUtils.GenerateHash(GetBlockHString(lastBlock, address));
//            System.out.println("hash "+hash);
//            System.out.println("last block hash "+lastBlock.Hash);
            if (Objects.equals(lastBlock.Hash, hash)) {
                return true;
            }else {
                return false;
            }
        }
        return false ;
    }

    public float GetBalance(String address){
        BlockData[] blocks = GetChain(address);
        float balance = 0;
        NetUtils netUtils = new NetUtils();

        // prepare request data 
        RequestData requestData = new RequestData();
        requestData.Action = "GET_BALANCE_SING";
        requestData.Balance.WalletAddress = address;

        // get user balance for current node
        BlockData block= GetLastBlock(address);
        balance = block.Balance;

 
                
        Consencus consencus = new Consencus();
        ArrayList<Float> datalist= new ArrayList<>();
        // send request for balance to other nodes 
        Node node = Node.getInstance();
        String[] allNodes =  node.GetAllNodes();
        for (String server: allNodes){
           ResponseData resposne= netUtils.SendRequest(server, requestData);
           System.out.println("node balance ccc   "+resposne.Data.get("Balance"));
           System.out.println("NOde Response  message   "+resposne.RespMessage);
           System.out.println("NOde Response  code   "+resposne.RespCode);
           System.out.println("NOde Response  balance   "+resposne.Balance);
        }
//    Consencus.Vote resultVote = consencus.Vote(datalist.toArray());
//    System.out.println("vote count : "+ resultVote.Count);
//    System.out.println("vote data : "+ resultVote.object);
        return balance;
    }

    // get balance function for nodes internally 
    // when a node needs a balance on a wallet, it does not need to consult other nodes
    public float GetBalanceSing(String address){
        BlockData block= GetLastBlock(address);
        return block.Balance;
        // get user balance for current node
    }

    // this function transfers money from one wallet to another
    public void Transfer(String senderAddress, String recieverAddress, float amount,
                         @Nullable boolean isBroadcasted, @Nullable String blockID) throws Exception {
        Wallet wallet = new Wallet();
        if(!wallet.IsWalletExists(senderAddress) && !wallet.IsWalletExists(recieverAddress)){
            throw new Exception("Wallet does not exist");
        }
        Error error = null;
        // check if sender has the amount of money in question
        if (!(GetBalance(senderAddress) >= amount)){
            throw new Exception("Insufficient balance");
        }

        // check if a users vault limit is reached
        if(!CheckWalletLimitOK(senderAddress, amount)){
            return;
        }

        BlockData senderLastBlock = GetLastBlock(senderAddress);
        BlockData recieverLastBlock = GetLastBlock(recieverAddress);


        // check if the transaction has occured on this ledger before

        // new blocks
        BlockData senderBlock = new BlockData();
        BlockData recieverBlock = new BlockData();
        String newBlockID = secUtils.GenerateHash(secUtils.GenerateRandString());

        senderBlock.Sender = senderAddress;
        senderBlock.Receiver = recieverAddress;
        senderBlock.Amount = amount;
        // senders new balance
        senderBlock.Balance = senderLastBlock.Balance - amount;
        senderBlock.CreatedAt = commonUtils.CurrentDate();
        senderBlock.PrevHash = senderLastBlock.Hash;
        senderBlock.ID = isBroadcasted?blockID:newBlockID;
        senderBlock.PrivateKeyHash = senderLastBlock.PrivateKeyHash;
        senderBlock.Hash = secUtils.GenerateHash(GetBlockHString(senderBlock, senderAddress));

        recieverBlock.Sender = senderAddress;
        recieverBlock.Receiver = recieverAddress;
        recieverBlock.Amount = amount;
        // reciever new balance
        recieverBlock.Balance = amount + recieverLastBlock.Balance;
        recieverBlock.ID = isBroadcasted?blockID:newBlockID;
        recieverBlock.PrevHash = recieverLastBlock.Hash;
        recieverBlock.CreatedAt = commonUtils.CurrentDate();
        recieverBlock.PrivateKeyHash = recieverLastBlock.PrivateKeyHash;
        recieverBlock.Hash = secUtils.GenerateHash(GetBlockHString(recieverBlock, recieverAddress));

        // add new blocks to the chain
        boolean oks =AddBlock(senderBlock, senderAddress);
        boolean okr = AddBlock(recieverBlock, recieverAddress);

        if (oks && okr){
            System.out.println("broadcasting transfer ...");
            // broadcast new blocks
            RequestData requestData = new RequestData();
            BroadCast broadCast = new BroadCast();
            requestData.Action ="TRANSFER";
            requestData.Transfer.IsBroadcasted = true;
            requestData.Transfer.Amount = amount;
            requestData.Transfer.ReceiverAddress = recieverAddress;
            requestData.Transfer.SenderAddress = senderAddress;
            requestData.Transfer.BlockID = senderBlock.ID;

            broadCast.Broadcast(requestData);
        } else {
            // send back an error response 
        }
    }


    public boolean CheckWalletLimitOK(String address, float amount)throws Exception{
        FileUtils fileUtils = new FileUtils();
        Object obj = fileUtils.ReadFileObj("data/"+address+"/data.bin");
        WalletData walletData = (WalletData) obj;
        CommonUtils commonUtils = new CommonUtils();
        String todaysDate = commonUtils.CurrentDate();
        if (!walletData.Vault){
            return true;
        }
        // check if the wallet open day is past
        try {
            if(new SimpleDateFormat("yyyy-MM-dd").parse(walletData.VaultOpenDate).before(new Date())){
                // this means its time
                return true;
            }else {
               // this means that its not yet time
                // check the limit
                BlockData[] blocks = GetChain(address);
                float sentToday = 0;
                for (BlockData block: blocks){
//                    System.out.println(block.Sender);
                    switch (walletData.LimitTime){
                        case "d":
                            if(new SimpleDateFormat("yyyy-MM-dd").parse(block.CreatedAt)
                                    .equals(new SimpleDateFormat("yyyy-MM-dd").parse(todaysDate))){
//                                System.out.println("todays block ...");
                                if (Objects.equals(block.Sender, address)){
                                    sentToday = sentToday + block.Amount;
                                }
                            }
                            break;
                        case "m":
//                            System.out.println("monthly");
                            if(new SimpleDateFormat("yyyy-MM").parse(block.CreatedAt)
                                    .equals(new SimpleDateFormat("yyyy-MM").parse(todaysDate))){
//                                System.out.println("todays block ...");
                                if (Objects.equals(block.Sender, address)){
                                    sentToday = sentToday + block.Amount;
                                }
                            }
                            break;
                        case "y":
                            if(new SimpleDateFormat("yyyy").parse(block.CreatedAt)
                                    .equals(new SimpleDateFormat("yyyy").parse(todaysDate))){
//                                System.out.println("todays block ...");
                                if (Objects.equals(block.Sender, address)){
                                    sentToday = sentToday + block.Amount;
                                }
                            }
                            break;
                    }
                }

//                System.out.println("spent today  "+ sentToday);
                String ttime = "";
                if (Objects.equals(walletData.LimitTime, "m")){
                    ttime = "monthly";
                }
                if (Objects.equals(walletData.LimitTime, "d")){
                    ttime = "daily";
                }
                if (Objects.equals(walletData.LimitTime, "y")){
                    ttime = "yearly";
                }
                if (sentToday+amount > walletData.VaultLimit){
//                    System.out.println("limit exceeded");
                    throw new Exception("You have exceeeded your limit of "+walletData.VaultLimit+ " "+ttime);
                }


            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean validateVault(WalletData walletData, float Amount){
        if (!walletData.Vault){
            return false;
        }
        // check if  the limit has been reached
        return true;
    }

}
