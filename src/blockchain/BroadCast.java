package src.blockchain;

import src.models.RequestData;
import src.utils.NetUtils;

public class BroadCast {

    public void Broadcast(RequestData requestData){
        Node node = Node.getInstance();
        String[] servers = node.GetAllNodes();
        NetUtils netUtils = new NetUtils();
//        requestData.CreateWallet.IsBroadcasted = true;
        for (String server: servers){
            netUtils.SendRequest(server, requestData);
        }
    }
    public void BCreateWallet(RequestData requestData){
        // when this broadcasts a createwallet event to other servers
        Node node = Node.getInstance();
        String[] servers = node.GetAllNodes();
        NetUtils netUtils = new NetUtils();
//        requestData.CreateWallet.IsBroadcasted = true;
        for (String server: servers){
            netUtils.SendRequest(server, requestData);
        }
    }

}
