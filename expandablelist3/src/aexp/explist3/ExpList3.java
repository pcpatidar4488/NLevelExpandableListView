package aexp.explist3;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.widget.SimpleExpandableListAdapter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class ExpList3 extends ExpandableListActivity
{
    static final String listdesc[][][][] = 
	{
        { // grey
          {  // lightgray
            { "grey", "lightgrey" },
            { "lightgrey","#D3D3D3" },
		    { "dimgrey","#696969" }
          },
          {  // darkgray
            { "grey", "darkgrey" },
		    { "sgi grey 92","#EAEAEA" }
          }
        },
        { // blue
          {  // lightblue
            { "blue", "lightblue" },
            { "dodgerblue 2","#1C86EE" }
          },
          {  // darkblue
            { "blue", "darkblue" },
		    { "steelblue 2","#5CACEE" },
            { "powderblue","#B0E0E6" }
          }
        },
        { // yellow
          {  // lightyellow
            { "yellow", "lightyellow" },
            { "yellow 1","#FFFF00" },
            { "gold 1","#FFD700" }
          },
          {  // darkyellow
            { "yellow", "darkyellow" },
		    { "darkgoldenrod 1","#FFB90F" }
          }
        },
        { // red
          {  // lightred
            { "red", "lightred" },
            { "indianred 1","#FF6A6A" }
          },
          {  // darkred
            { "red", "darkred" },
		    { "firebrick 1","#FF3030" },
            { "maroon","#800000" }
          }
        }
	};

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.main);
		colorExpListAdapter =
			new ColorExpListAdapter(
				this,
                getExpandableListView(),
                listdesc
			);
		setListAdapter( colorExpListAdapter );
    }

    private ColorExpListAdapter colorExpListAdapter;
}
