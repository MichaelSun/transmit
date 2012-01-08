package com.internal.transmit.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.internal.transmit.MessageInfo;

public class DatabaseOperator {
    private static final String TAG = "DatabaseOperator";
    private static final boolean DEBUG = true;
    
    private static DatabaseOperator gDatabaseOperator;
    private static Object mObj = new Object();
    
    private DatabaseProxy mDBProxy;
    private boolean mInit;

    public static DatabaseOperator getInstance() {
        if (gDatabaseOperator == null) {
            synchronized (mObj) {
                if (gDatabaseOperator == null) {
                    gDatabaseOperator = new DatabaseOperator();
                }
            }
        }
        
        return gDatabaseOperator;
    }
    
    public void init(Context context) {
        if (!mInit) {
            mDBProxy = DatabaseProxy.getDBInstance(context);
            mInit = true;
        }
    }
    
    public ArrayList<MessageInfo> queryInbox() {
        ArrayList<MessageInfo> ret = new ArrayList<MessageInfo>();
        
        Cursor cursor = null;
        try {
            cursor = mDBProxy.query(DataBaseConfig.INBOX_TABLE_NAME, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    MessageInfo info = new MessageInfo();
                    info.phone = cursor.getString(cursor.getColumnIndex(DataBaseConfig.INBOX_TABLE_PHONE));
                    info.content = cursor.getString(cursor.getColumnIndex(DataBaseConfig.INBOX_TABLE_CONTENT));
                    info.time = cursor.getString(cursor.getColumnIndex(DataBaseConfig.INBOX_TABLE_TIME));

                    ret.add(info);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        
        return ret;
    }
    
    public void insertInboxInfo(MessageInfo info) {
        ContentValues values = new ContentValues();
        values.put(DataBaseConfig.INBOX_TABLE_PHONE, info.phone);
        values.put(DataBaseConfig.INBOX_TABLE_CONTENT, info.content);
        values.put(DataBaseConfig.INBOX_TABLE_TIME, info.time);
        
        mDBProxy.insert(DataBaseConfig.INBOX_TABLE_NAME, values);
    }
    
    public void deleteInboxInfo(MessageInfo info) {
        if (info == null || info.time == null) {
            return;
        }
        
        String selection = DataBaseConfig.INBOX_TABLE_TIME + "=?";
        String[] selectionArgs = new String[]{ info.time };
        
        mDBProxy.delete(DataBaseConfig.INBOX_TABLE_NAME, selection, selectionArgs);
    }
    
    public ArrayList<MessageInfo> queryOutbox() {
        ArrayList<MessageInfo> ret = new ArrayList<MessageInfo>();
        
        Cursor cursor = null;
        try {
            cursor = mDBProxy.query(DataBaseConfig.OUTBOX_TABLE_NAME, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    MessageInfo info = new MessageInfo();
                    info.phone = cursor.getString(cursor.getColumnIndex(DataBaseConfig.OUTBOX_TABLE_PHONE));
                    info.content = cursor.getString(cursor.getColumnIndex(DataBaseConfig.OUTBOX_TABLE_CONTENT));
                    info.time = cursor.getString(cursor.getColumnIndex(DataBaseConfig.OUTBOX_TABLE_TIME));

                    ret.add(info);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        
        return ret;
    }

    public void insertOutboxInfo(MessageInfo info) {
        ContentValues values = new ContentValues();
        values.put(DataBaseConfig.OUTBOX_TABLE_PHONE, info.phone);
        values.put(DataBaseConfig.OUTBOX_TABLE_CONTENT, info.content);
        values.put(DataBaseConfig.OUTBOX_TABLE_TIME, info.time);
        
        mDBProxy.insert(DataBaseConfig.OUTBOX_TABLE_NAME, values);
    }
    
    public void deleteOutboxInfo(MessageInfo info) {
        if (info == null || info.time == null) {
            return;
        }
        
        String selection = DataBaseConfig.OUTBOX_TABLE_TIME + "=?";
        String[] selectionArgs = new String[]{ info.time };
        
        mDBProxy.delete(DataBaseConfig.OUTBOX_TABLE_NAME, selection, selectionArgs);
    }
    
    public void insertReceivedSMSLog(boolean isCDMA, String phone, String content, String time) {
        ContentValues values = new ContentValues();
        values.put(DataBaseConfig.CDMA_LOG_TABLE_PHONE, phone);
        values.put(DataBaseConfig.CDMA_LOG_TABLE_CONTENT, content);
        values.put(DataBaseConfig.CDMA_LOG_TABLE_TIME, time);
        
        mDBProxy.insert(isCDMA 
                            ? DataBaseConfig.CMDA_LOG_TABLE_NAME 
                            : DataBaseConfig.GSM_LOG_TABLE_NAME, 
                        values);
    }
    
    public ArrayList<MessageInfo> getRecievedSMSLog(boolean isCDMA) {
        ArrayList<MessageInfo> ret = new ArrayList<MessageInfo>();
        
        Cursor cursor = null;
        try {
            String tableName = isCDMA ? DataBaseConfig.CMDA_LOG_TABLE_NAME : DataBaseConfig.GSM_LOG_TABLE_NAME;
            cursor = mDBProxy.query(tableName, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    MessageInfo info = new MessageInfo();
                    info.phone = cursor.getString(cursor.getColumnIndex(DataBaseConfig.CDMA_LOG_TABLE_PHONE));
                    info.content = cursor.getString(cursor.getColumnIndex(DataBaseConfig.CDMA_LOG_TABLE_CONTENT));
                    info.time = cursor.getString(cursor.getColumnIndex(DataBaseConfig.CDMA_LOG_TABLE_TIME));

                    ret.add(info);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        
        return ret;
    }
    
    private DatabaseOperator() {
    }
    
    private void LOGD(String text) {
        if (DEBUG) {
            Log.d(TAG, text);
        }
    }
}
