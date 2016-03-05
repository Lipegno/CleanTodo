package lipeapps.quintal.com.cleantodo;

import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import layout.TodoAppWidget;

/**
 * Created by Filipe on 27/02/2016.
 */
public class TodoViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<ContentValues> _content;
    private Context _ctx;
    private DBManager _manager;
    private int appWidgetId;

    public TodoViewFactory(Context ctx, Intent intent){
        this._ctx = ctx;

        Log.d("ViewFact","View factory created");

        _manager = DBManager.getDBManager(_ctx);
        _content = _manager.getNotes();

    //    for(String str:_content)
    //        Log.d("CONTENT",str);

        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return _content.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row  = new RemoteViews(_ctx.getPackageName(),R.layout.todo_item);
        row.setTextViewText(R.id.todo_item_text,_content.get(position).getAsString("item"));

        Intent i=new Intent();
        Bundle extras=new Bundle();

        extras.putString(TodoAppWidget.EXTRA_WORD, _content.get(position).getAsString("item"));
        i.putExtras(extras);
        row.setOnClickFillInIntent(R.id.todo_item_text, i);

        Log.d("CONTENT", "drawing items");

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
}
