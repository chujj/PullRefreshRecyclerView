package com.ssc.widget.example;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ssc.widget.pullrefreshrecyclerview.PullRefreshRecyclerView;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private PullRefreshRecyclerView mPullRefreshRecyclerView;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mPullRefreshRecyclerView == null) {

            mPullRefreshRecyclerView = (PullRefreshRecyclerView) this.findViewById(R.id.recyclerview);
            mPullRefreshRecyclerView.setAdapter(mAdapter = new MyAdapter());
            mPullRefreshRecyclerView.setListener(new PullRefreshRecyclerView.PullRefreshRecyclerViewListener() {
                @Override
                public void onRefresh() {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.refresh();
                            mPullRefreshRecyclerView.setRefreshing(false);
                        }
                    }, 2000);
                }
            });
        }
    }

    public static class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        LinkedList<Integer> mDatas = new LinkedList<>();

        public MyAdapter() {
            mDatas.clear();
            for (int i = 0; i < 20; i++) {
                mDatas.add(Integer.valueOf(i));
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView rootview = new TextView(parent.getContext());
            return new ViewHolder(rootview);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((TextView)holder.itemView).setText(Integer.toString(mDatas.get(position)));
        }


        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        public void refresh() {
            int start = mDatas.get(0) - 1;
            mDatas.clear();
            for (int i = start; i < start + 20; i++) {
                mDatas.add(Integer.valueOf(i));
            }
            this.notifyDataSetChanged();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }

    }
}
