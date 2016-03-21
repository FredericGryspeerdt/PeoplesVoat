package team4.howest.be.androidapp.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import team4.howest.be.androidapp.R;

/**
 * Created by Frederic on 21/12/2015.
 */
public class SearchAdapter extends ArrayAdapter<String> {

    private Context context;
    int resource, textViewResourceId;
    private List<String> items, tempItems, suggestions;
    private Filter filter;

    public SearchAdapter(Context context, int resource, int textViewResourceId, List<String> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<String>(items); // this makes the difference.
        suggestions = new ArrayList<String>();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.search_item, parent, false);
        }

        String subverse = items.get(position);
        if (subverse != null) {
            TextView lblName = (TextView) view.findViewById(R.id.lbl_name);
            if (lblName != null) {
                lblName.setText(subverse);
            }
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return subverseFilter;
    }

    Filter subverseFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = resultValue.toString();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (String subverse : tempItems) {
                    if (subverse.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(subverse);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<String> filterList = (ArrayList<String>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (String subverse : filterList) {
                    add(subverse);
                    notifyDataSetChanged();
                }
            }
        }
    };
}





