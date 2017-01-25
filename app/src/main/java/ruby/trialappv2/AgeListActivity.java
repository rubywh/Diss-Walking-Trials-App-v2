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


/* An Activity that allows the user to select their age from a list of options*/

public class AgeListActivity extends Activity implements WearableListView.ClickListener {

    public final static String AGE_CHOICE = "Age Chosen";
    public final static String GENDER_CHOICE = "Gender Chosen";
    public final static String TRIAL_CHOICE = "Trial Chosen";
    private static ArrayList<Integer> listItems;

    /* Set up an ArrayList of items to be added as list labels*/
    static {
        listItems = new ArrayList<Integer>();
        for (int i = 10; i <= 90; i++) {
            listItems.add(i);
        }
    }

    private WearableListView lv;
    private String gender;

    private String trial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        TextView title = (TextView) findViewById(R.id.title);
        Intent intent = getIntent();
        gender = intent.getStringExtra(GenderListActivity.GENDER_CHOICE);
        trial = intent.getStringExtra(GenderListActivity.TRIAL_CHOICE);


        System.out.println(gender);
        lv = (WearableListView) findViewById(R.id.List1);
        lv.setAdapter(new AgeListActivity.MyAdapter(AgeListActivity.this));
        lv.setClickListener(AgeListActivity.this);
        title.setText("Please select your Age");
    }

    /*Get the age selected and pass to next activity*/
    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        String age = listItems.get(viewHolder.getLayoutPosition()).toString();
        Intent intent = new Intent(this, HeightListActivity.class);
        intent.putExtra(GENDER_CHOICE, gender);
        intent.putExtra(AGE_CHOICE, age);
        intent.putExtra(TRIAL_CHOICE, trial);
        startActivity(intent);

    }

    @Override
    public void onTopEmptyRegionClick() {

    }

    public class MyAdapter extends WearableListView.Adapter {

        private final LayoutInflater inflater;

        public MyAdapter(Context c) {
            inflater = LayoutInflater.from(c);
        }

        //Create new views for list items
        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new WearableListView.ViewHolder(inflater.inflate(R.layout.row_simple_item_layout, null));
        }

        //Replace contents of a list item
        @Override
        public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int i) {
            //retrieve text view
            TextView view = (TextView) viewHolder.itemView.findViewById(R.id.textView);
            //replace text
            view.setText(listItems.get(i).toString());
            viewHolder.itemView.setTag(i);
        }

        public int getThisItem(int i) {
            return listItems.get(i);
        }

        @Override
        public int getItemCount() {
            return listItems.size();
        }
    }

}
