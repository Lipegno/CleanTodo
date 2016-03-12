package layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RemoteViews;

import java.util.ArrayList;

import lipeapps.quintal.com.cleantodo.DBManager;
import lipeapps.quintal.com.cleantodo.MainActivity;
import lipeapps.quintal.com.cleantodo.R;
import lipeapps.quintal.com.cleantodo.TodoService;

/**
 * Implementation of App Widget functionality.
 */
public class TodoAppWidget extends AppWidgetProvider {

    public final static String EXTRA_WORD = "dawg";
    public static final String WIDGET_IDS_KEY ="appwidgetproviderwidgetids";
    public static final String WIDGET_DATA_KEY ="appwidgetproviderwidgetdata";

    ListView _todoList;
    EditText _note;
    DBManager _manager;
    ArrayList<String> _content;


    @Override
    public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        Log.e("WIDGET","onUpdate widget called dawg");

        for (int i=0; i<appWidgetIds.length; i++) {
            Log.e("WIDGET","onUpdate widget FOR called dawg");
            Intent svcIntent=new Intent(ctxt, TodoService.class);

            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget=new RemoteViews(ctxt.getPackageName(),
                    R.layout.todo_app_widget);

            widget.setRemoteAdapter(R.id.todo_view_widget,
                    svcIntent);

            Intent clickIntent=new Intent(ctxt, MainActivity.class);
            PendingIntent clickPI=PendingIntent
                    .getActivity(ctxt, 0,
                            clickIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT);

            widget.setPendingIntentTemplate(R.id.todo_view_widget, clickPI);

            appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
        }
        super.onUpdate(ctxt, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
       if (intent.hasExtra(WIDGET_IDS_KEY)) {
            int[] ids = intent.getExtras().getIntArray(WIDGET_IDS_KEY);
            this.onUpdate(context, AppWidgetManager.getInstance(context), ids);
        } else super.onReceive(context, intent);

        Log.e("New data receiver", "calhow aqui");
    }

 /*
    @Override
    public void onEnabled(Context context) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
//
//
//        _manager = DBManager.getDBManager(context);
//        _manager.getNotes();
//        _content = _manager.getNotes();
//        _myAdapter = new TodoAdapter(context, _content,R.layout.todo_item,inflater);
//        _todoList.setAdapter(_myAdapter);
//        _myAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private class TodoAdapter extends ArrayAdapter {

        private final LayoutInflater _inflater;
        private final int _resource;

        public TodoAdapter(Context context, ArrayList<String> content, int resource, LayoutInflater inflater) {
            super(context, resource,content);
            _inflater = inflater;
            _resource = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            if(convertView == null){
                convertView = _inflater.inflate(_resource, null);
            }
            Log.i("MAIN", "printing note");
            TextView item = (TextView) convertView.findViewById(R.id.todo_item_text);
            item.setText(_content.get(position));
            return convertView;
        }
    }*/

   /*private class NewDataBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)){
                updateWidgetItemList(context);
            }
        }

        private void updateWidgetItemList(Context context) {
//            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.todo_app_widget);
//
//            //REMEMBER TO ALWAYS REFRESH YOUR BUTTON CLICK LISTENERS!!!
//            remoteViews.setOnClickPendingIntent(R.id.widget_button, MyWidgetProvider.buildButtonPendingIntent(context));
//
//            pushWidgetUpdate(context.getApplicationContext(), remoteViews);

            Log.e("New data receiver", "calhow aqui");
        }

    }*/
}

