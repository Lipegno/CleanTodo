package lipeapps.quintal.com.cleantodo;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import layout.TodoAppWidget;

/**
 * Created by Filipe on 27/02/2016.
 */
public class TodoViewFactory extends BroadcastReceiver  implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<ContentValues> _content;
    private Context _ctx;
    private DBManager _manager;
    private int appWidgetId;
    private SharedPreferences _sp;

    private static final String VIEW_FACT = "View factory";

    public TodoViewFactory(Context ctx, Intent intent){
        this._ctx = ctx;

        Log.d("ViewFact","View factory created");

        _manager = DBManager.getDBManager(_ctx);
        _content = _manager.getNotes();

    //    for(String str:_content)
    //        Log.d("CONTENT",str);
     //   IntentFilter filter = new IntentFilter();
      //  filter.addAction("RefreshDBIntent");
     //   _ctx.registerReceiver(this,filter);

        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        _sp = _ctx.getSharedPreferences("cleantodoprefs",Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate() {
        Log.e(VIEW_FACT, "On create called");

    }

    @Override
    public void onDataSetChanged() {
        _content = _manager.getNotes();
    }

    @Override
    public void onDestroy() {
        Log.e(VIEW_FACT,"On destroy called");
    }

    @Override
    public int getCount() {
        Log.e(VIEW_FACT,"On getCount called");
        return _content.size();
    }

    void adjustTextSize(RemoteViews v,int size){

        if(size==1){
            v.setFloat(R.id.todo_item_text, "setTextSize", _ctx.getResources().getDimension(R.dimen.textSizeSmall));
        }
        else if(size==2){
            v.setFloat(R.id.todo_item_text, "setTextSize", _ctx.getResources().getDimension(R.dimen.textSizeMedium));
        }
        else if(size==3){
            v.setFloat(R.id.todo_item_text, "setTextSize", _ctx.getResources().getDimension(R.dimen.textSizeBig));
        }

    }
    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row  = new RemoteViews(_ctx.getPackageName(),R.layout.todo_item);
        row.setTextViewText(R.id.todo_item_text,_content.get(position).getAsString("item"));
        int textSize =  _sp.getInt("textSize",0);
        adjustTextSize(row, textSize);

       if(_content.get(position).getAsInteger("done")==1)
           row.setInt(R.id.todo_item_text, "setPaintFlags", Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
       else
           row.setInt(R.id.todo_item_text, "setPaintFlags", 0);

        Intent i=new Intent();
        Bundle extras=new Bundle();

        extras.putString(TodoAppWidget.EXTRA_WORD, _content.get(position).getAsString("item"));
        i.putExtras(extras);
        row.setOnClickFillInIntent(R.id.todo_item_text, i);

       // Log.d("CONTENT", "drawing items "+_content.get(position).getAsString("item"));

        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("View factory", "Data received "+intent.toString());
        _manager = DBManager.getDBManager(_ctx);
        _content = _manager.getNotes();

    }
}
