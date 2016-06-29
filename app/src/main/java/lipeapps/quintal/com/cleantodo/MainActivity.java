package lipeapps.quintal.com.cleantodo;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import layout.TodoAppWidget;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainAct";
    ListView _todoList;
    EditText _note;
    DBManager _manager;
    TodoAdapter _myAdapter;
    ArrayList<ContentValues> _content;
    private String _note_to_delete;
    SharedPreferences _sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);

        _todoList = (ListView)findViewById(R.id.todo_view);
        _note     = (EditText)findViewById(R.id.note_text);
        _content = new ArrayList<ContentValues>();


        _manager = DBManager.getDBManager(getApplicationContext());
        _content = _manager.getNotes();
        _myAdapter = new TodoAdapter(this, _content,R.layout.item,getLayoutInflater());
        _todoList.setAdapter(_myAdapter);
        _myAdapter.notifyDataSetChanged();

        _sp = this.getSharedPreferences("cleantodoprefs",Context.MODE_PRIVATE);

    }

    @Override
    protected void onStop() {
        super.onStop();
        updateWidgets();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.small) {
            Log.d(TAG, "small selected");
            _sp.edit().putInt("textSize",1).commit();
        }else if(item.getItemId()==R.id.medium) {
            Log.d(TAG, "medium selected");
            _sp.edit().putInt("textSize",2).commit();
        }else if(item.getItemId()==R.id.big) {
            Log.d(TAG, "big selected");
            _sp.edit().putInt("textSize",3).commit();
        }
        _myAdapter.notifyDataSetChanged();
        return true;
    }
/*private void updateList(){
        _content = _manager.getNotes();
        printContentDebug();
        _myAdapter.notifyDataSetChanged();

    }*/

    private void printContentDebug(){
        //Log.e(TAG, " ---------------------");
        for(ContentValues c:_content){
            Log.i(TAG,"item -> "+c.getAsString("item"));
        }
       // Log.e(TAG, " ---------------------");
    }

    public void handlePlusClick(View v) {
        String note = _note.getText().toString();

        if(!note.equals("")) {
            Log.i("MAIN", "adding note");
            _manager.addNote(note);
            ContentValues cv = new ContentValues();
            cv.put("item",note);
            cv.put("done",0);
            cv.put("tms", System.currentTimeMillis());
            addItemList(_content,cv);
            _myAdapter.notifyDataSetChanged();
            _note.setText("");
            updateWidgets();
            printContentDebug();
        }
    }

    private void addItemList(ArrayList<ContentValues> list, ContentValues item){
        int index = 0;
        for(int i=0;i<list.size();i++){
            if(list.get(i).getAsInteger("done")==0)
                index++;
            else
                break;
        }
        list.add(index, item);

    }

    private void updateWidgets(){

        final AppWidgetManager mgr = AppWidgetManager.getInstance(getApplicationContext());
        final ComponentName cn = new ComponentName(getApplicationContext(), TodoAppWidget.class);
        mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.todo_view_widget);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Note Options");
        menu.add(0, v.getId(), 0, "delete");

        _note_to_delete = ((TextView)v).getText().toString();

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals("delete")) {

            Log.e(TAG,"Before delete");
            printContentDebug();
            int last_index = _content.size()-1;
            for(int i=0; i<_content.size();i++){
                Log.d(TAG,"looking for "+_note_to_delete+" found "+_content.get(i).getAsString("item"));
                if(_content.get(i).getAsString("item").equals(_note_to_delete)) {
                    _manager.deleteNote(_note_to_delete);
                    _content.remove(i);
                    _myAdapter.notifyDataSetChanged();
                    break;
                }

            }
        }

        return true;

    }

    private class TodoAdapter extends ArrayAdapter{

        private final  LayoutInflater _inflater;
        private final int _resource;

        public TodoAdapter(Context context, ArrayList<ContentValues> content, int resource, LayoutInflater inflater) {
            super(context, resource,content);
            _inflater = inflater;
            _resource = resource;
        }

        View adjustTextSize(View v,int size){

            if(size==1){
                ((TextView)v).setTextSize(getResources().getDimension(R.dimen.textSizeSmall));
            }else if(size==2){
                ((TextView)v).setTextSize(getResources().getDimension(R.dimen.textSizeMedium));
            }else if(size==3){
                ((TextView)v).setTextSize(getResources().getDimension(R.dimen.textSizeBig));
            }

            return v;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent){

            if(convertView == null){
                convertView = _inflater.inflate(_resource, null);
            }

            if(position<_content.size()) {

                final TextView item = (TextView) convertView.findViewById(R.id.main_item_text);
                int textSize =  _sp.getInt("textSize",0);
                adjustTextSize(item,textSize);
                item.setText(_content.get(position).getAsString("item"));
                final CheckBox check = (CheckBox) convertView.findViewById(R.id.item_done);

                Log.i(TAG, "Dealing with " + _content.get(position).getAsString("item") + " status: " + _content.get(position).getAsString("done"));

                if (_content.get(position).getAsInteger("done") == 1) {
                    check.setChecked(true);
                    item.setPaintFlags(item.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else if (_content.get(position).getAsInteger("done") == 0){
                    check.setChecked(false);
                    item.setPaintFlags(item.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }



                check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (check.isChecked()){
                            //check.setChecked(true);
                            _manager.updateNote(_content.get(position).getAsString("item"), 1);
                            _content.get(position).put("done", 1);
                            item.setPaintFlags(item.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        } else {
                            _manager.updateNote(_content.get(position).getAsString("item"), 0);
                            _content.get(position).put("done", 0);
                            item.setPaintFlags(item.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                          //  check.setChecked(false);
                        }

                    }
                });

                registerForContextMenu(item);
            }
            return convertView;
        }

    }
}
