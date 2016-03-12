package layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    AppWidgetManager _widget_manager;


    @Override
    public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        Log.e("WIDGET","onUpdate widget called dawg");

        for (int i=0; i<appWidgetIds.length; i++) {
            Log.e("WIDGET", "onUpdate widget FOR called dawg");
            Intent svcIntent=new Intent(ctxt, TodoService.class);

            IntentFilter filter = new IntentFilter();
            filter.addAction("RefreshDBIntent");
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
                            PendingIntent.FLAG_UPDATE_CURRENT);

            widget.setPendingIntentTemplate(R.id.todo_view_widget, clickPI);
            appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
        }
        super.onUpdate(ctxt, appWidgetManager, appWidgetIds);
    }

}

