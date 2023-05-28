package com.example.upbmaps;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.upbmaps.ClassInfo;

import java.util.ArrayList;
import java.util.List;

public class ClassListAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private List<ClassInfo> originalClassList;
    private List<ClassInfo> filteredClassList;
    private ClassFilter classFilter;


    public ClassListAdapter(Context context, List<ClassInfo> classList) {
        this.context = context;
        this.originalClassList = classList;
        this.filteredClassList = classList;
    }

    @Override
    public int getCount() {
        return filteredClassList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredClassList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item_class, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textViewSubject = convertView.findViewById(R.id.textViewSubject);
            viewHolder.textViewClassName = convertView.findViewById(R.id.textViewClassName);
            viewHolder.textViewPlaces =  convertView.findViewById(R.id.textViewPlaces);
            viewHolder.textViewTime = convertView.findViewById(R.id.textViewTime);
            viewHolder.textViewDate = convertView.findViewById(R.id.textViewDate);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ClassInfo classInfo = filteredClassList.get(position);

        viewHolder.textViewSubject.setText("SUBJECT : " + classInfo.getSubject());
        viewHolder.textViewSubject.setTypeface(null, Typeface.BOLD);
        viewHolder.textViewClassName.setText("CLASS NUMBER : " + classInfo.getClassName());
        viewHolder.textViewClassName.setTypeface(null, Typeface.BOLD);
        viewHolder.textViewPlaces.setText("NUMBER OF PLACES : " + classInfo.getClassPlaces());
        viewHolder.textViewPlaces.setTypeface(null, Typeface.BOLD);
        viewHolder.textViewTime.setText("FROM : " + classInfo.getHourFrom() + "  TO : " + classInfo.getHourTo());
        viewHolder.textViewTime.setTypeface(null, Typeface.BOLD);
        viewHolder.textViewDate.setText("DATE : " + classInfo.getDate());
        viewHolder.textViewDate.setTypeface(null, Typeface.BOLD);

        return convertView;
    }

    static class ViewHolder {
        TextView textViewSubject;
        TextView textViewClassName;
        TextView textViewTime;
        TextView textViewDate;
        TextView textViewPlaces;
    }

    @Override
    public Filter getFilter() {
        if (classFilter == null) {
            classFilter = new ClassFilter();
        }
        return classFilter;
    }

    private class ClassFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                filterResults.count = originalClassList.size();
                filterResults.values = originalClassList;
            } else {
                String searchQuery = constraint.toString().toLowerCase();

                List<ClassInfo> filteredClasses = new ArrayList<>();
                for (ClassInfo classInfo : originalClassList) {
                    if (classInfo.getClassName().toLowerCase().contains(searchQuery) ||
                            classInfo.getSubject().toLowerCase().contains(searchQuery)) {
                        filteredClasses.add(classInfo);
                    }
                }

                filterResults.count = filteredClasses.size();
                filterResults.values = filteredClasses;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredClassList = (List<ClassInfo>) results.values;
            notifyDataSetChanged();
        }
    }
}
