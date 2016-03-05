package lipeapps.quintal.com.cleantodo;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created by Filipe on 27/02/2016.
 */
public class TodoService extends RemoteViewsService {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SERVICE", "onCreate called");


    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        Log.d("SERVICE", "onGetViewFactory called");

        return (new TodoViewFactory(this.getApplicationContext(),
                intent));
    }
}