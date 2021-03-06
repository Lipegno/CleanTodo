package lipeapps.quintal.com.cleantodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Filipe on 22/02/2016.
 */

public final class DBManager{

    DBHelper _helper;
    private static final String TODO_TABLE = "todo_items";
    private static final String DB_NAME = "todo_db.db";
    private static final int DB_VERSION = 8;
    private static Context _ctx;

    private DBManager(){
        try{
            if(_helper == null)
                _helper = new DBHelper(_ctx);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static class DBManagerHolder{
        private static final DBManager INSTANCE = new DBManager();
    }

    public static DBManager getDBManager(Context ctx){
        _ctx = ctx;
        return DBManagerHolder.INSTANCE;
    }

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String  sql = "CREATE TABLE "+TODO_TABLE+" (" +
                "id  integer PRIMARY KEY AUTOINCREMENT, " +
                "note text NOT NULL, " +
                "done int NOT NULL, " +
                "tms real NOT NULL" +
                ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "DROP TABLE "+TODO_TABLE;
        db.execSQL(sql);

        sql = "CREATE TABLE "+TODO_TABLE+" (" +
                "id  integer PRIMARY KEY AUTOINCREMENT, " +
                "note text NOT NULL, " +
                "done int NOT NULL, " +
                "tms real NOT NULL" +
                ")";

        Log.i("DBManager", "updating database");

        db.execSQL(sql);
    }
}

    public synchronized boolean addNote(String note){
        SQLiteStatement stm = null;
        long tms            = System.currentTimeMillis();
        SQLiteDatabase _db  = _helper.getReadableDatabase();

        String sql = "INSERT INTO "+TODO_TABLE+"(note,done,tms)" +
                " VALUES('"+note+"',"+0+","+tms+")";

        stm = _db.compileStatement(sql);

        if(stm.executeInsert()>0)
            return true;
        else
            return false;
    }

    public synchronized ArrayList<ContentValues> getNotes(){

        SQLiteDatabase    db = _helper.getReadableDatabase();
        ArrayList<ContentValues> result_temp  = new ArrayList<ContentValues>();

        String sql = "SELECT note,done FROM "+TODO_TABLE+" order by tms desc";

        Cursor c = db.rawQuery(sql, null);

        while(c.moveToNext()) {
            ContentValues cv = new ContentValues();
            cv.put("item",c.getString(0));
            cv.put("done", c.getInt(1));

            if(c.getInt(1)==0)
              result_temp.add(0,cv);
            else
               result_temp.add(cv);

        }


        return result_temp;
    }

    public synchronized void updateNote(String note, int done){

        SQLiteDatabase db = _helper.getWritableDatabase();

        String sql = "UPDATE "+TODO_TABLE+" SET done="+done+" WHERE "+"note ='"+note+"'";
        db.execSQL(sql);
    }

    public synchronized void deleteNote(String note){
        SQLiteDatabase db = _helper.getWritableDatabase();
        String sql = "DELETE FROM "+TODO_TABLE+" WHERE "+"note ='"+note+"'";
        db.execSQL(sql);
    }

}
