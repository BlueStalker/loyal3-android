package com.loyal3.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.loyal3.R;

import java.util.List;

public class SingleSelectionExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private String header;
    // child data in format of header title, child title
    private List<String> children;
    private LayoutInflater inflater;
    private int selection = -1;

    public SingleSelectionExpandableListAdapter(Context context, String header, List<String> children) {
        this._context = context;
        this.header = header;
        this.children = children;
        inflater = LayoutInflater.from(_context);
    }

    @Override
    public Object getChild(int groupPosition, int childPos) {
        return children.get(childPos);
    }

    @Override
    public long getChildId(int groupPosition, int childPos) {
        return childPos;
    }

    public void setSelected(int selection) {
        this.selection = selection;
    }

    public int getSelection() {
        return selection;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View view, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);
        if (selection == childPosition) view = inflater.inflate(R.layout.select_list_item_selected, null);
        else view = inflater.inflate(R.layout.select_list_item, null);

        TextView text = (TextView) view.findViewById(R.id.select_label);

        text.setText(childText);
        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return header;
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.select_list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.group_label);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
