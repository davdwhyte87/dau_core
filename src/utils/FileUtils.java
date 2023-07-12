package src.utils;

import src.blockchain.WalletData;

import java.io.*;

public class FileUtils {

    public boolean CreateFileObject(Serializable object, String filename){

        try
        {
            //Saving of object in a file
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(file);

            // Method for serialization of object
            out.writeObject(object);

            out.close();
            file.close();

//            System.out.println("Object has been serialized");


        } catch(IOException ex)
        {
            System.out.println("IOException is caught");
            return false;
        }
        return true;
    }

    public Object ReadFileObj(String filename){
        WalletData walletData = null;
        Object object= null;
        try
        {
            // Reading the object from a file
            FileInputStream file = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(file);

            // Method for deserialization of object
            object = (Object)in.readObject();

//            in.close();
            file.close();

//            System.out.println("Object has been deserialized ");

        } catch(IOException ex)
        {
            System.out.println("IOException is caught while reading data");
        } catch(ClassNotFoundException ex)
        {
            System.out.println("ClassNotFoundException is caught");
        }

        return object;
    }
}
