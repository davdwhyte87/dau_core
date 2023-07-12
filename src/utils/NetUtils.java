package src.utils;

import com.google.gson.Gson;
import src.models.RequestData;
import src.models.ResponseData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class NetUtils {
    // this takes a request object and sends it over a tcp network and get response
    public ResponseData SendRequest(String Ip, RequestData requestData){
        ResponseData responseData = new ResponseData();
        ResponseData errResponse = new ResponseData();
        Gson gson = new Gson();
        try {
            String[] addr = Ip.split(":");
//                System.out.print(addr[0] + "   "+addr[1]);
            Socket socket = new Socket(addr[0], Integer.parseInt(addr[1]));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            // convert req data object to string

            String reqString = gson.toJson(requestData);
            // write request data
            writer.println(reqString);
            System.out.println("sending data to other server ( "+Ip+" )");
           

            InputStream input = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int read;
            read = input.read(buffer);
            System.out.println(read +"read data size");

            if (read != -1) {
                System.out.println("Time to read response .......");
                String outData = new String(buffer, 0, read);
                System.out.println(outData);
                //get data from req string
                responseData = gson.fromJson(outData, ResponseData.class);
            }


            // close all sockets 
            writer.close();
            input.close();
            socket.close();
        } catch (Exception e) {
//                e.printStackTrace();
            errResponse.RespCode = 500;
            errResponse.RespMessage = e.getMessage();
            System.out.println(e);
            return errResponse;
        }
        return responseData;
    }


    // this function takes a request socket steam and sends a response through the output stream
    public void SendResponse(PrintWriter writer, ResponseData responseData){
        Gson gson = new Gson();
        String reqString = gson.toJson(responseData);
        writer.println(reqString);
        writer.close();
    }
}
