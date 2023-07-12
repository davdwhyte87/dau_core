package src.blockchain;

import com.google.gson.Gson;
import io.github.cdimascio.dotenv.Dotenv;
import src.models.CreateWalletReq;
import src.models.RequestData;
import src.models.ResponseData;
import src.models.WalletSyncData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import src.utils.NetUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
    private static Node nodeInstance = null;

    public static Node getInstance(){
        if (nodeInstance == null ){
            nodeInstance = new Node();
        }
        return nodeInstance;
    }
    Dotenv dotenv = Dotenv.load();
    public Socket sSocket;
    // this function fetches all the nodes that this server has access to.
    public String[] GetAllNodes(){
        JSONParser jsonParser = new JSONParser();
        JSONObject serverListObj = null;
        try {
            serverListObj = (JSONObject) jsonParser.parse(new FileReader("server_list.json"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ArrayList<String> nodeServers = new ArrayList<String>();
        JSONArray servers = (JSONArray) serverListObj.get("servers");
        for (Object server:servers){
//            System.out.println((String) server);
            nodeServers.add((String) server);
        }

        return nodeServers.toArray(new String[0]);
    }



    public void StartServer(){
        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(dotenv.get("PORT", "3000")))) {

            // int SDK_INT = android.os.Build.VERSION.SDK_INT;
            // if (SDK_INT > 8) {
            //     StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
            //             .permitAll().build();
            //     StrictMode.setThreadPolicy(policy);
            // }
            // InetAddress IP=InetAddress.getLocalHost();
            // Log.d("SERVER","ip address is :"+ IP.getHostAddress());
            // System.out.println("Server is listening on port " + 4000);
            // Log.d("SERVER","Server is listening on port " + 4000 );

            InetAddress IP=InetAddress.getLocalHost();
            System.out.println(IP.getHostAddress());

            while (true) {
                Socket socket = serverSocket.accept();

                System.out.println("New client connected");
                // Log.d("SERVER", "New client connected");

                OutputStream output = socket.getOutputStream();
                InputStream input = socket.getInputStream();

                PrintWriter writer = new PrintWriter(output, true);
                byte[] buffer = new byte[1024];
                int read;
                read = input.read(buffer);
                //


//                ByteArrayInputStream binputStream = new ByteArrayInputStream(buffer);
//                ObjectInputStream objectStream = new ObjectInputStream(input);
//                RequestData requestData = (RequestData) objectStream.readObject();
//                objectStream.close();
//                if (requestData !=null){
//                    System.out.println(requestData.Action);
//                }

//                System.out.println(input.read());
               

                if (read!= -1){
                    String outData = new String(buffer, 0, read);
//                    RequestData requestData = SerializationUtils.deserialize(input);
//                    System.out.println(requestData.Action);
                    // parse data from tcp in text format to json object
                    JSONParser jsonParser = new JSONParser();
                    JSONObject requestObjJson = null;
                    try {
//                        requestObjJson = (JSONObject) jsonParser.parse(outData);
//                        String action = (String) requestObjJson.get("Action");
//                        JSONObject reqData = (JSONObject) requestObjJson.get("Data");
//                        String walletAddress = (String) reqData.get("WalletAddress");
                        Gson gson = new Gson();
                        RequestData requestData = gson.fromJson(outData, RequestData.class);
                        System.out.print("Request data:"+requestData);
                        // perform action based on "actions" in reques

                        Route(requestData, writer);
                        input.close();
//                        System.out.println(action+"{{{{{}}}}}}}");
//                        System.out.println(walletAddress+"wallet address {{{}}}}}}}");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    System.out.println(outData);
                }
//                writer.println("kdkmd");
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            // Log.d("SERVER","Server exception: "+ ex.getMessage());
            ex.printStackTrace();
        }
    }


    public void Route(RequestData requestData, PrintWriter writer){
        switch (requestData.Action){
            case "CREATE_WALLET":
                System.out.println("CREATE WALLET ACTION>>>>>>>>>");
//                HandleGetServers(writer);
                HandleCreateWallet(writer, requestData);
                break;
            case "TRANSFER":
                System.out.println("TRANSFER ACTION>>>>>>>>>");
                HandleTransfer(writer, requestData);
                break;
            case "GET_BALANCE":
                System.out.println("GET BALANCE ACTION >>>>>>>>>");
                HandleGetBalance(writer, requestData);
                break;
            case "GET_BALANCE_SING":
                System.out.println("GET BALANCE ACTION SING >>>>>>>>>");
                HandleGetBalanceSing(writer, requestData);
                break;
            case "GET_NETWORK_LIST":
                break;
            case "GET_NODE_WALLETS":
                System.out.println("GET NODE WALLETS ACTION >>>>>>>>>>>>>");
                HandleGetWallets(writer, requestData);
                break;
        }
    }


    public void HandleGetBalance(PrintWriter writer, RequestData requestData){
        String address = requestData.Balance.WalletAddress;
        Blockchain blockchain = new Blockchain();
        // get balance for this server
        float balance =blockchain.GetBalance(address);

        NetUtils netUtils = new NetUtils();
        // prepare response
        ResponseData responseData = new ResponseData();
        responseData.RespCode = 200;
        responseData.RespMessage ="oK";
        responseData.Balance = balance;
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("Balance", balance);
        responseData.Data = dataMap;
      
        // send reponse
        netUtils.SendResponse(writer, responseData);
    }

    public void HandleGetBalanceSing(PrintWriter writer, RequestData requestData){
        System.out.print("fetching balance... for node ...");
        String address = requestData.Balance.WalletAddress;
        Blockchain blockchain = new Blockchain();
        // get balance for this server
        float balance =blockchain.GetBalanceSing(address);
        System.out.print("balance ... for node ..."+ balance);
        NetUtils netUtils = new NetUtils();
        // prepare response
        ResponseData responseData = new ResponseData();
        responseData.RespCode = 200;
        responseData.RespMessage ="oK";
        responseData.Balance = balance;
        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap.put("Balance", balance);
        responseData.Data = dataMap;
      
        // send reponse
        netUtils.SendResponse(writer, responseData);
    }

    public void HandleCreateWallet(PrintWriter writer, RequestData requestData){
        Wallet wallet = new Wallet();
        BroadCast broadCast = new BroadCast();
        CreateWalletReq createWalletReq = new CreateWalletReq();
        createWalletReq.WalletName = requestData.CreateWallet.WalletName;
        createWalletReq.Password = requestData.CreateWallet.Password;
        createWalletReq.WalletVault = requestData.CreateWallet.IsVault;
        createWalletReq.LimitTime = requestData.CreateWallet.LimitTime;
        createWalletReq.VaultLimit = requestData.CreateWallet.VaultLimit;
        createWalletReq.VaultOpenDate = requestData.CreateWallet.VaultOpenDate;
        System.out.println("creating wallet with password "+requestData.CreateWallet.Password);
        String walletAddress = wallet.CreateWallet(requestData.CreateWallet);

        NetUtils netUtils = new NetUtils();
        ResponseData responseData = new ResponseData();
        responseData.RespCode = 200;
        responseData.RespMessage ="oK";
        Map<String,Object> data= new HashMap<>();
        data.put("WalletName", walletAddress );
        System.out.println(data);
        responseData.Data = data;
        netUtils.SendResponse(writer, responseData);
    }

    public void HandleGetServers(PrintWriter writer){
        String[] servers = GetAllNodes();
        RequestData requestData = new RequestData();
        requestData.NodeServers = servers;
        requestData.Action = "RESP";
        // convert request object to string (json)
        Gson gson = new Gson();
        String reqString = gson.toJson(requestData);
        writer.println(reqString);
        System.out.println("sending resp");
    }

    public void HandleGetWallets(PrintWriter writer, RequestData requestData){
        Wallet wallet = new Wallet();
        NetUtils netUtils = new NetUtils();
        WalletSyncData[] wallets = wallet.GetWallets();

        ResponseData responseData = new ResponseData();
        responseData.NodeWallets = wallets;
        netUtils.SendResponse(writer, responseData );
    }

    public void HandleTransfer(PrintWriter writer, RequestData requestData){
        Blockchain blockchain = new Blockchain();
        NetUtils netUtils = new NetUtils();
        if(requestData.Transfer.IsBroadcasted){
            System.out.println("IS BROADCASTED");
//            System.out.println(requestData.Transfer.SenderBlockID +"Sender block id");

            if (blockchain.IsBlockExists(requestData.Transfer.SenderAddress, requestData.Transfer.BlockID)){
                System.out.println("ALREADY EXISTS ");
                ResponseData responseData = new ResponseData();
                responseData.RespCode = 200;
                responseData.RespMessage ="Ok";
                netUtils.SendResponse(writer, responseData);
                return;
            }

        }
        try {
            blockchain.Transfer(requestData.Transfer.SenderAddress,
                    requestData.Transfer.ReceiverAddress,
                    requestData.Transfer.Amount, requestData.Transfer.IsBroadcasted, requestData.Transfer.BlockID);
            ResponseData responseData = new ResponseData();
            responseData.RespCode = 200;
            responseData.RespMessage ="Ok";
            netUtils.SendResponse(writer, responseData);
        } catch(Exception e){
            System.out.println("Error transfering coins .....  "+ e);
            ResponseData responseData = new ResponseData();
            responseData.RespCode = 500;
            responseData.RespMessage ="error sys ....."+e.getMessage();
            netUtils.SendResponse(writer, responseData);
        }

    }


    public void HandleIssueNewCoins(PrintWriter writer, RequestData requestData){
        Wallet wallet = new Wallet();
        wallet.IssueNewCoins(requestData.NewCoins.amount, requestData.NewCoins.password);

    }

    public void Initialize(){
        // this function starts up a new node

        // get list of all servers from initial peers
        String[] servers = GetAllNodes();
        for (String server: servers){
            try {
                String[] addr = server.split(":");
//                System.out.print(addr[0] + "   "+addr[1]);
                Socket socket = new Socket(addr[0], Integer.parseInt(addr[1]));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                // convert object to string
                RequestData requestData = new RequestData();
                requestData.Action ="CREATE_WALLET";
                requestData.WalletAddress = "okfkvnjs909sn";
                Gson gson = new Gson();
                String reqString = gson.toJson(requestData);
                // write request data
                writer.println(reqString);
                System.out.println("sending data to other server");

                //clean up
//                writer.flush();
//                writer.close();

                InputStream input = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int read;
                read = input.read(buffer);
                System.out.println(read +"read data size");

//                while (readSize ==-1){
//                    readSize = input.read();
//                    // keeep waiting till we get response
//                }
                if (read != -1) {
                    System.out.println("Time to read response .......");

                    String outData = new String(buffer, 0, read);
                    System.out.println(outData);

                    // get server list from node
//                    String[] servers = GetAllNodes();

                    //get data from req string
//                    Gson gson = new Gson();
                    RequestData responseData = gson.fromJson(outData, RequestData.class);

                    JSONObject jsonObject = null;
                    JSONParser jsonParser = new JSONParser();
                    JSONObject serverListObj = null;
                    try {
                        //read json data on servers
                        serverListObj = (JSONObject) jsonParser.parse(new FileReader("server_list.json"));
                        ArrayList<String> nodeServers = new ArrayList<String>();
                        JSONArray serverList = (JSONArray) serverListObj.get("servers");
                        // add new servers gotten from response
                        serverList.addAll(List.of(responseData.NodeServers));
                        serverListObj.replace("servers",serverList);
                        // write new updated json object
                        FileWriter fileWriter = new FileWriter("server_list.json");
                        fileWriter.write(serverListObj.toJSONString());
                        fileWriter.close();
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }

                }


            } catch (Exception e) {
//                e.printStackTrace();
                System.out.println(e);
            }
            System.out.print(server);
      }
    }

    // get wallet data from nodes

    public void getNodeWallets(){
        NetUtils netUtils = new NetUtils();
        String[] servers = GetAllNodes();
        Wallet wallet = new Wallet();
        ArrayList<WalletSyncData> walletList = new ArrayList<WalletSyncData>();
        for (String server: servers){
            RequestData requestData = new RequestData();
            requestData.Action = "GET_NODE_WALLETS";

           ResponseData responseData = netUtils.SendRequest(server, requestData);
           WalletSyncData[] wallets = responseData.NodeWallets;

           // loop through wallet and check if there is any it already exists in walletList if not, add it
           for (WalletSyncData syncData : wallets){
              if (!walletList.contains(syncData) ){
                  walletList.add(syncData);
              }
           }
        }

        // create all the wallets
        wallet.CreateMultipleWallets(walletList.toArray(new WalletSyncData[0]));
    }
}
