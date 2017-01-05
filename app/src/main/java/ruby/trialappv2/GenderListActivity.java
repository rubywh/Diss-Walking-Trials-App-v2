package ruby.trialappv2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ruby__000 on 23/12/2016.
 */

public class GenderListActivity extends Activity implements WearableListView.ClickListener {

    public final static String GENDER_CHOICE = "Gender Chosen";
    private static ArrayList<String> listItems;

    static {
        listItems = new ArrayList<String>();
        listItems.add("Male");
        listItems.add("Female");
    }

    private WearableListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        listView = (WearableListView) findViewById(R.id.List1);
        listView.setAdapter(new GenderListActivity.MyAdapter(GenderListActivity.this));
        listView.setClickListener(GenderListActivity.this);


    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        String msg = listItems.get(viewHolder.getLayoutPosition());
        Intent intent = new Intent(this, AgeListActivity
                .class);
        intent.putExtra(GENDER_CHOICE, msg);
        startActivity(intent);
    }

    @Override
    public void onTopEmptyRegionClick() {

    }

    private class MyAdapter extends WearableListView.Adapter {

        private final LayoutInflater inflater;

        private MyAdapter(Context c) {
            inflater = LayoutInflater.from(c);
        }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new WearableListView.ViewHolder(inflater.inflate(R.layout.row_simple_item_layout, null));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int i) {
            TextView view = (TextView) viewHolder.itemView.findViewById(R.id.textView);
            view.setText(listItems.get(i).toString());
            viewHolder.itemView.setTag(i);
        }

        @Override
        public int getItemCount() {
            return listItems.size();
        }
    }
}

