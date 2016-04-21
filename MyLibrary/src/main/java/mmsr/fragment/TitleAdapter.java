package mmsr.fragment;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TitleAdapter extends ArrayAdapter<Title> {
	private final Context context;
	private final List<Title> titles;
	private int resource;
	
	
	public TitleAdapter(Context context, int resource,
			 List<Title> objects) {
		super(context, resource, objects);
		
		this.context = context;
		this.titles = objects;
		this.resource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(resource, parent, false);
		
		TextView textViewTitle = (TextView)rowView.findViewById(R.id.textViewTitle);
		TextView textViewID = (TextView)rowView.findViewById(R.id.textViewID);
		
		File dir = Environment.getExternalStorageDirectory(); 
		File picFile = new File(dir, "/mmsr/"+  titles.get(position).getID() +"/pic/cover.png");

		
		Bitmap bitmap = BitmapFactory.decodeFile(picFile.toString()); //This gets the image
		
		ImageView imageCover = (ImageView)rowView.findViewById(R.id.imageCover);
		imageCover.setImageBitmap(bitmap);
		
		textViewTitle.setText(titles.get(position).getTitle());
		textViewID.setText(""+titles.get(position).getID());
		return rowView;
	}
	
	
}
