package com.internal.transmit.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.internal.transmit.utils.Config;
import com.internal.transmit.utils.SettingManager;

class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "info.db";
    private static final int DATABASE_VERSION = 2;
    
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        if (!SettingManager.getInstance().getIsCenter()) {
            db.execSQL(DataBaseConfig.INBOX_DATABASE_CREATE);
            db.execSQL(DataBaseConfig.OUTBOX_DATABASE_CREATE);
        } else {
            db.execSQL(DataBaseConfig.RECEIVED_CDMA_LOG_TABLE);
            db.execSQL(DataBaseConfig.RECEIVED_GSM_LOG_TABLE);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion <= 2) {
            db.execSQL("DROP TABLE IF EXISTS " + DataBaseConfig.INBOX_DATABASE_CREATE);
            db.execSQL("DROP TABLE IF EXISTS " + DataBaseConfig.OUTBOX_DATABASE_CREATE);
            db.execSQL("DROP TABLE IF EXISTS " + DataBaseConfig.RECEIVED_CDMA_LOG_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DataBaseConfig.RECEIVED_GSM_LOG_TABLE);
            onCreate(db);
        }
    }

    private boolean checkColomnIsExist(SQLiteDatabase db, String table, String checkColomn) {
        boolean isExist = true;
        String tableName = "sqlite_master";
        String[] columns = new String[]{"sql"};
        String selection = "tbl_name" + "=?" + " AND type" + "=?";
        String[] selectionArgs = new String[]{table, "table"};
        Cursor cursor = null;
        try {
            cursor = db.query(tableName, columns, selection, selectionArgs, null, null, null);//("select sql from sqlite_master where tbl_name='chat_log' and type='table'");
            if(cursor != null){
                if(cursor.moveToNext()){
                    String sql = cursor.getString(0);
                    if(!sql.contains(checkColomn)){
                        isExist = false;
                    }else{
                        isExist = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(cursor != null){
                cursor.close();
            }
        }
        
        return isExist;
    }
}
