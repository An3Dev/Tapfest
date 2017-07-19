package an3enterprises.tapfest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Settings extends AppCompatActivity {

    ArrayAdapter settings;
    ListView lv;
    List<String> g = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);



        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_settings);
        lv = (ListView) findViewById(R.id.settings_listview);
        g.add("\nRemove ads\n");
        settings = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, g);
        lv.setAdapter(settings);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (g.get(position).matches("\nRemove ads\n")){
                }
            }
        });
    }

    public void removeAds(){
    }


}
