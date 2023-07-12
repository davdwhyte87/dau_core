package src.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {
    public String CurrentDate(){
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
}
