package de.theknut.xposedgelsettings.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import de.theknut.xposedgelsettings.R;

// http://stackoverflow.com/a/19283600/809277
public abstract class ActionBarListActivity extends ActionBarActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listactivity);

        getListView().setCacheColorHint(getResources().getColor(R.color.grid));
        getListView().setBackgroundColor(getResources().getColor(R.color.grid));
    }

    protected ListView getListView() {
        if (mListView == null) {
            mListView = (ListView) findViewById(android.R.id.list);
        }
        return mListView;
    }

    protected void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }

    protected ListAdapter getListAdapter() {
        ListAdapter adapter = getListView().getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            return ((HeaderViewListAdapter)adapter).getWrappedAdapter();
        } else {
            return adapter;
        }
    }

    protected void onListItemClick(ListView lv, View v, int position, long id) {
        getListView().getOnItemClickListener().onItemClick(lv, v, position, id);
    }

}