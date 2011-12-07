package com.internal.transmit.db;

class DataBaseConfig {

    public static final String SPLITOR = ";";
    
    public static final String INBOX_DATABASE_CREATE = "create table inbox "
            + "(_id INTEGER primary key autoincrement, "
            + "phone TEXT not null, "
            + "content TEXT not null, "
            + "time TEXT not null)";

    public static final String INBOX_TABLE_NAME = "inbox";
    public static final String INBOX_TABLE_PHONE = "phone";
    public static final String INBOX_TABLE_CONTENT = "content";
    public static final String INBOX_TABLE_TIME = "time";
    
    public static final String OUTBOX_DATABASE_CREATE = "create table outbox "
            + "(_id INTEGER primary key autoincrement, "
            + "phone TEXT not null, "
            + "content TEXT not null, "
            + "time TEXT not null)";

    public static final String OUTBOX_TABLE_NAME = "outbox";
    public static final String OUTBOX_TABLE_PHONE = "phone";
    public static final String OUTBOX_TABLE_CONTENT = "content";
    public static final String OUTBOX_TABLE_TIME = "time";
}
