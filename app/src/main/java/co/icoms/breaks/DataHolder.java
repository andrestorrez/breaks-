package co.icoms.breaks;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import co.icoms.breaks.adapters.RequestAdapter;
import co.icoms.breaks.models.Request;

/**
 * Created by escolarea on 12/13/16.
 */
public class DataHolder {
    private static DataHolder ourInstance = new DataHolder();

    public static DataHolder getInstance() {
        return ourInstance;
    }

    private ArrayList<Request> requests;
    private WeakReference<RequestAdapter> requestAdapter;

    public ArrayList<Request> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<Request> requests) {
        this.requests = requests;
    }

    public void setRequestAdapter(RequestAdapter requestAdapter) {
        this.requestAdapter = new WeakReference<>(requestAdapter);
    }

    public void updateRequestAdapter(){
        RequestAdapter adapter = requestAdapter.get();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }
}
