package net.frindex.caloriescounterandroidjava.common;
/**
 *
 * File: WriteToErrorLog.java
 * Version 1.0.0
 * Date 18.02.2022
 * Copyright (c) 2022 Sindre Andre Ditlefsen
 * Website: https://ditlef.net
 * License: http://opensource.org/licenses/gpl-license.php GNU Public License
 *
 */
import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.frindex.caloriescounterandroidjava.dao.DBAdapter;

public class WriteToErrorLog {

    private final Context context;


    public WriteToErrorLog(Context context) {
        this.context = context;
    }

    public void writeToErrorLog(String sourceClass, String sourceMethod, String messageType, String errorMessage){
        /* Database */
        DBAdapter db = new DBAdapter(context);
        db.open();

        DateFormat dfhhmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateyyyyMMdddfhhmmss = dfhhmmss.format(Calendar.getInstance().getTime());

        String classSQL = db.quoteSmart(sourceClass);
        String methodSQL = db.quoteSmart(sourceMethod);
        String errorSQL = db.quoteSmart(errorMessage);
        String typeSQL = db.quoteSmart(messageType);

        String q = "INSERT INTO app_error_log (_id, datetime, class, method, type, error) " +
                "VALUES (NULL, " +
                "'" + dateyyyyMMdddfhhmmss + "', " +
                classSQL + ", " +
                methodSQL + ", " +
                typeSQL + ", " +
                errorSQL + "" +
                ")";
        db.rawQuery(q);

        db.close();


    }
}
