package lipeapps.quintal.com.cleantodo;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import layout.TodoAppWidget;

public class MainActivity extends AppCompatActivity {

    ListView _todoList;
    EditText _note;
    DBManager _manager;
    TodoAdapter _myAdapter;
    ArrayList<ContentValues> _content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _todoList = (ListView)findViewById(R.id.todo_view);
        _note     = (EditText)findViewById(R.id.note_text);
        _content = new ArrayList<ContentValues>();


        _manager = DBManager.getDBManager(getApplicationContext());
        _content = _manager.getNotes();
        _myAdapter = new TodoAdapter(this, _content,R.layout.item,getLayoutInflater());
        _todoList.setAdapter(_myAdapter);
        _myAdapter.notifyDataSetChanged();
    }

    private void updateList(){
        _content = _manager.getNotes();
        _myAdapter.notifyDataSetChanged();


    }

    public void handlePlusClick(View v){
        String note = _note.getText().toString();

        if(!note.equals("")) {
            Log.i("MAIN", "adding note");
            _manager.addNote(note);
            ContentValues cv = new ContentValues();
            cv.put("item",note);
            cv.put("done",0);
            _content.add(0,cv);
            _myAdapter.notifyDataSetChanged();
            _note.setText("");
            //updateList();
            updateWidgets();
        }
    }

    private void updateWidgets(){
       /* AppWidgetManager man = AppWidgetManager.getInstance(getApplicationContext());
        int[] ids = man.getAppWidgetIds(
                new ComponentName(getApplicationContext(), TodoAppWidget.class));
        Intent updateIntent = new Intent();
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(TodoAppWidget.WIDGET_IDS_KEY, ids);
        //  updateIntent.putExtra(MyWidgetProvider.WIDGET_DATA_KEY, data);
        sendBroadcast(updateIntent);*/
//
//
//        Intent updateIntent2 = new Intent();
//        updateIntent2.setAction("RefreshDBIntent");
//       // updateIntent2.putExtra(TodoAppWidget.WIDGET_IDS_KEY, ids);
//        //  updateIntent.putExtra(MyWidgetProvider.WIDGET_DATA_KEY, data);
////        sendBroadcast(updateIntent2);
//        //send to factory
  //      man.notifyAppWidgetViewDataChanged(ids, R.layout.todo_app_widget);

        final AppWidgetManager mgr = AppWidgetManager.getInstance(getApplicationContext());
        final ComponentName cn = new ComponentName(getApplicationContext(), TodoAppWidget.class);
        mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.todo_view_widget);

    }


    private class TodoAdapter extends ArrayAdapter{

        private final  LayoutInflater _inflater;
        private final int _resource;

        public TodoAdapter(Context context, ArrayList<ContentValues> content, int resource, LayoutInflater inflater) {
            super(context, resource,content);
            _inflater = inflater;
            _resource = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent){

            if(convertView == null){
                convertView = _inflater.inflate(_resource, null);
            }
            Log.i("MAIN", "printing note");
            final TextView item = (TextView) convertView.findViewById(R.id.main_item_text);
            item.setText(_content.get(position).getAsString("item"));
            final CheckBox check = (CheckBox)convertView.findViewById(R.id.item_done);

            if(_content.get(position).getAsInteger("done")==1){
                check.setChecked(true);
                item.setPaintFlags(item.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }else {

                check.setChecked(false);
                item.setPaintFlags(item.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked){
                        // check.setChecked(true);
                        _manager.updateNote(_content.get(position).getAsString("item"), 1);
                        _content.get(position).put("done",1);
                        item.setPaintFlags(item.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }else{
                        _manager.updateNote(_content.get(position).getAsString("item"),0);
                        _content.get(position).put("done",0);
                        item.setPaintFlags(item.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                }
            });

            return convertView;
        }

    }
}
