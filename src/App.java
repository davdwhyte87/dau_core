package src;

import src.blockchain.Blockchain;
import io.github.cdimascio.dotenv.Dotenv;
import src.models.CreateWalletReq;
import src.models.WalletSyncData;
import src.utils.SecUtils;
import src.blockchain.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

class App {

  public static void main(String[] args) {
    Node node = Node.getInstance();
    node.StartServer();
    
//  node.GetAllNodes();

//    Consencus consencus = new Consencus();
//    ArrayList<String> datalist= new ArrayList<>();
//
//    datalist.add("no");
//    datalist.add("yes");
//    datalist.add("no");
//    datalist.add("yes");
//    datalist.add("no");
//    datalist.add("yes");
//    datalist.add("yes");
//    datalist.add("yes");
//    datalist.add("yes");
//    Consencus.Vote resultVote = consencus.Vote(datalist.toArray());
//    System.out.println("vote count : "+ resultVote.Count);
//    System.out.println("vote data : "+ resultVote.object);

    Scanner input = new Scanner(System.in);
    System.out.print("Press Enter to quit...");
    input.nextLine();
  }
}
