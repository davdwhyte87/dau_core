package src.models;

import java.util.HashMap;
import java.util.Map;

public class ResponseData {
    public String[] NodeServers;
    public String RespMessage;
    public Integer RespCode;
    public Map<String, Object> Data;
    public float Balance;
    public WalletSyncData[] NodeWallets;
}
