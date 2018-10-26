package aexp.explist3;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.CheckBox;
import android.util.Log;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class ColorExpListAdapter extends BaseExpandableListAdapter {

    public ColorExpListAdapter(Context context, 
                            ExpandableListView topExpList,
                            String listdesc[][][][] ) { 
        this.context = context;
        this.topExpList = topExpList;
// blue <- level1 group
//   darkblue <- level2 group
//      pureblue #0000FF <- level2 child
// listdesc is formatted as the following:
// {
//   { // belongs to a level1 group [group1][x][y]
//     { // belongs to level2 group [group1][group2_0]
//       { // belongs to child items in [group1][group2_0] 
//         { "group1_name", "group2_0_name" }    // [group1][0][0]
//         { "color_name1", "rgb1" }        // [group1][group2][item1] ...
//         { "color_name2", "rgb2" }        // [group1][group2][item2] ...
//       }
//     { // belongs to level2 group [group1][group2_1]
//       { // belongs to child items in [group1][group2_1]
//         { "group1_name", "group2_1_name" }    // [group1][1][0]
//          ...
//       }
//     }
//     ...
//   }
//   ...
// }
        this.listdesc = listdesc;
        inflater = LayoutInflater.from( context );
		listViewCache = new DebugExpandableListView[ listdesc.length ];
    }

    public Object getChild(int groupPosition, int childPosition) {
        return listdesc[groupPosition][childPosition];
    }

    public long getChildId(int groupPosition, int childPosition) {
        return (long)( groupPosition*1024+childPosition );  // Max 1024 children per group
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Log.d( LOG_TAG, "getChildView: groupPositon: "+groupPosition+"; childPosition: "+childPosition );
		View v = null;
		if( listViewCache[groupPosition] != null )
            v = listViewCache[groupPosition];
        else {
            DebugExpandableListView dev = new DebugExpandableListView( context );
			dev.setRows( calculateRowCount( groupPosition, null ) );
           	dev.setAdapter( 
			        new DebugSimpleExpandableListAdapter(
				        context,
				        createGroupList( groupPosition ),	// groupData describes the first-level entries
				        R.layout.child3_row,	// Layout for the first-level entries
				        new String[] { KEY_COLORNAME },	// Key in the groupData maps to display
				        new int[] { R.id.childname },		// Data under "colorName" key goes into this TextView
				        createChildList( groupPosition ),	// childData describes second-level entries
				        R.layout.child3_row,	// Layout for second-level entries
				        new String[] { KEY_SHADENAME, KEY_RGB },    // Keys in childData maps to display
				        new int[] { R.id.childname, R.id.rgb }	// Data under the keys above go into these TextViews
                    )
          	);
            dev.setOnGroupClickListener( new Level2GroupExpandListener( groupPosition ) );
			listViewCache[groupPosition] = dev;
			v = dev;
		}
        return v;
    }

    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    public Object getGroup(int groupPosition) {
        return listdesc[groupPosition][0][0][0];        
    }

    public int getGroupCount() {
        return listdesc.length;
    }

    public long getGroupId(int groupPosition) {
        return (long)( groupPosition*1024 );  // To be consistent with getChildId
    } 

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Log.d( LOG_TAG, "getGroupView: groupPositon: "+groupPosition+"; isExpanded: "+isExpanded );
        View v = null;
        if( convertView != null )
            v = convertView;
        else
            v = inflater.inflate(R.layout.group_row, parent, false); 
        String gt = (String)getGroup( groupPosition );
		TextView colorGroup = (TextView)v.findViewById( R.id.groupname );
		if( gt != null )
			colorGroup.setText( gt );
        return v;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    } 

    public void onGroupCollapsed (int groupPosition) {} 
    public void onGroupExpanded(int groupPosition) {}

/**
  * Creates a level2 group list out of the listdesc array according to
  * the structure required by SimpleExpandableListAdapter. The resulting
  * List contains Maps. Each Map contains one entry with key "colorName" and
  * value of an entry in the listdesc array.
  * @param level1 Index of the level1 group whose level2 subgroups are listed.
  */
	private List createGroupList( int level1 ) {
        ArrayList result = new ArrayList();
	    for( int i = 0 ; i < listdesc[level1].length ; ++i ) {
	        HashMap m = new HashMap();
	        m.put( KEY_COLORNAME,listdesc[level1][i][0][1] );
	    	result.add( m );
	    }
	    return (List)result;
    }

/**
  * Creates the child list out of the listdesc array according to the
  * structure required by SimpleExpandableListAdapter. The resulting List
  * contains one list for each group. Each such second-level group contains
  * Maps. Each such Map contains two keys: "shadeName" is the name of the
  * shade and "rgb" is the RGB value for the shade.
  * @param level1 Index of the level1 group whose level2 subgroups are included in the child list.
  */
    private List createChildList( int level1 ) {
	    ArrayList result = new ArrayList();
	    for( int i = 0 ; i < listdesc[level1].length ; ++i ) {
// Second-level lists
	        ArrayList secList = new ArrayList();
	        for( int n = 1 ; n < listdesc[level1][i].length ; ++n ) {
	            HashMap child = new HashMap();
		        child.put( KEY_SHADENAME, listdesc[level1][i][n][0] );
	            child.put( KEY_RGB, listdesc[level1][i][n][1] );
		        secList.add( child );
	        }
	        result.add( secList );
	    }
	    return result;
    }

// Calculates the row count for a level1 expandable list adapter. Each level2 group counts 1 row (group row) plus any child row that
// belongs to the group
    private int calculateRowCount( int level1, ExpandableListView level2view ) {
        int level2GroupCount = listdesc[level1].length;
        int rowCtr = 0;
        for( int i = 0 ; i < level2GroupCount ; ++i ) {
            ++rowCtr;       // for the group row
			if( ( level2view != null ) && ( level2view.isGroupExpanded( i ) ) )
				rowCtr += listdesc[level1][i].length - 1;	// then add the children too (minus the group descriptor)
        }
		return rowCtr;
    }

    private Context context;
    private String listdesc[][][][];
    private LayoutInflater inflater;
    private ExpandableListView topExpList;
	private DebugExpandableListView listViewCache[];
    private static final String KEY_COLORNAME = "colorName";
    private static final String KEY_SHADENAME = "shadeName";
    private static final String KEY_RGB = "rgb";
    private static final String LOG_TAG = "ColorExpListAdapter";

	class Level2GroupExpandListener implements ExpandableListView.OnGroupClickListener {
		public Level2GroupExpandListener( int level1GroupPosition ) {
			this.level1GroupPosition = level1GroupPosition;
		}

       	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
       		if( parent.isGroupExpanded( groupPosition ) )
            	parent.collapseGroup( groupPosition );
        	else
           		parent.expandGroup( groupPosition );
			if( parent instanceof DebugExpandableListView ) {
				DebugExpandableListView dev = (DebugExpandableListView)parent;
				dev.setRows( calculateRowCount( level1GroupPosition, parent ) );
			}
           	Log.d( LOG_TAG, "onGroupClick" );
           	topExpList.requestLayout();
          	return true;
     	}

		private int level1GroupPosition;
	}
}
