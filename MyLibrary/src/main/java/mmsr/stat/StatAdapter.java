package mmsr.stat;

import java.util.List;

import mmsr.fragment.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StatAdapter extends ArrayAdapter<ReadRecord>{

	private final Context context;
	private final List<ReadRecord> titles;
	private int resource;
	
	public StatAdapter(Context context, int resource,
			 List<ReadRecord> objects) {
		super(context, resource, objects);
		
		this.context = context;
		this.titles = objects;
		this.resource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(resource, parent, false);
		
		TextView textViewDateTime = (TextView)rowView.findViewById(R.id.textViewDateTime);
		TextView textViewData = (TextView)rowView.findViewById(R.id.textViewData);
		
		
		textViewDateTime.setText("ID: " + titles.get(position).getR_id() + 
				" Date:" + titles.get(position).getR_date() + 
				" Time:" +  titles.get(position).getR_time());
		textViewData
			.setText("EN:" + titles.get(position).getR_en() +
					" BM:" + titles.get(position).getR_bm() + 
					" CN:" + titles.get(position).getR_cn() +
					" TM:" + titles.get(position).getR_tm());
		
		return rowView;
	}

}
