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


/* An Activity that allows the user to select their gender from a list of options*/

public class GenderListActivity extends Activity implements WearableListView.ClickListener {

    public final static String GENDER_CHOICE = "Gender Chosen";
    public final static String TRIAL_CHOICE = "Trial Chosen";
    private static ArrayList<String> listItems;

    /* Set up an ArrayList of items to be added as list labels*/
    static {
        listItems = new ArrayList<String>();
        listItems.add("Male");
        listItems.add("Female");
    }

    String trial;
    private WearableListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText("Please select your Gender");

        listView = (WearableListView) findViewById(R.id.List1);
        listView.setAdapter(new GenderListActivity.MyAdapter(GenderListActivity.this));
        listView.setClickListener(GenderListActivity.this);

        Intent received = getIntent();
        trial = received.getStringExtra(AgeListActivity.TRIAL_CHOICE);

    }

    /*Get the gender selected and pass to next activity*/
    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        String msg = listItems.get(viewHolder.getLayoutPosition());
        Intent intent = new Intent(this, AgeListActivity
                .class);
        intent.putExtra(GENDER_CHOICE, msg);
        intent.putExtra(TRIAL_CHOICE, trial);
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


        /* Set up the listView */
        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new WearableListView.ViewHolder(inflater.inflate(R.layout.row_simple_item_layout, null));
        }


        /*Set the text in the listView*/
        @Override
        public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int i) {
            TextView view = (TextView) viewHolder.itemView.findViewById(R.id.textView);
            view.setText(listItems.get(i));
            viewHolder.itemView.setTag(i);
        }

        @Override
        public int getItemCount() {
            return listItems.size();
        }
    }
}

