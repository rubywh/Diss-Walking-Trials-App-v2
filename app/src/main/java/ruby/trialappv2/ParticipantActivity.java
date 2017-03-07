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

public class ParticipantActivity extends Activity implements WearableListView.ClickListener {

    public final static String PARTICIPANT_NUMBER = "Participant Number";
    public final static String TRIAL_CHOICE = "Trial Chosen";
    private static ArrayList<Integer> listItems;

    /* Set up an ArrayList of items to be added as list labels*/
    static {
        listItems = new ArrayList<>(50);
        for (int i = 1; i <= 50; i++) {
            listItems.add(i);
        }
    }

    private WearableListView lv;

    private String trial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        TextView title = (TextView) findViewById(R.id.title);
        Intent intent = getIntent();
        trial = intent.getStringExtra(SelectTrialType.TRIAL_CHOICE);

        lv = (WearableListView) findViewById(R.id.List1);
        lv.setAdapter(new ParticipantActivity.MyAdapter(ParticipantActivity.this));
        lv.setClickListener(ParticipantActivity.this);
        title.setText("Please select your participant number");
    }

    /*Get the age selected and pass to next activity*/
    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        String participantNo = listItems.get(viewHolder.getLayoutPosition()).toString();
        Intent intent = new Intent(this, SenseActivity.class);
        intent.putExtra(TRIAL_CHOICE, trial);
        intent.putExtra(PARTICIPANT_NUMBER, participantNo);
        startActivity(intent);
        finish();

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
