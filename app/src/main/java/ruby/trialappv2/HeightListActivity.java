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

public class HeightListActivity extends Activity implements WearableListView.ClickListener {
    public final static String AGE_CHOICE = "Age Chosen";
    public final static String GENDER_CHOICE = "Gender Chosen";
    public final static String HEIGHT_CHOICE = "Height Chosen";
    private static ArrayList<Integer> listItems;

    static {
        listItems = new ArrayList<Integer>();
        for (int i = 142; i < 210; i++) {
            listItems.add(i);
        }
    }

    private WearableListView listView;
    private String gender;
    private String age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);


        listView = (WearableListView) findViewById(R.id.List1);
        listView.setAdapter(new MyAdapter(HeightListActivity.this));
        listView.setClickListener(HeightListActivity.this);

        Intent received = getIntent();
        gender = received.getStringExtra(AgeListActivity.GENDER_CHOICE);
        age = received.getStringExtra(AgeListActivity.AGE_CHOICE);

    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        String height = listItems.get(viewHolder.getLayoutPosition()).toString();
        Intent intent = new Intent(this, SenseActivity.class);
        intent.putExtra(GENDER_CHOICE, gender);
        intent.putExtra(AGE_CHOICE, age);
        intent.putExtra(HEIGHT_CHOICE, height);
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
