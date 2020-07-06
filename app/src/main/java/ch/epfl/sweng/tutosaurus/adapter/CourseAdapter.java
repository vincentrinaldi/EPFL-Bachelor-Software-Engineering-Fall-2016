package ch.epfl.sweng.tutosaurus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ch.epfl.sweng.tutosaurus.R;
import ch.epfl.sweng.tutosaurus.model.Course;

/**
 * Array Adapter that is used to populate listviews with the names of the courses and their icon
 */
public class CourseAdapter extends ArrayAdapter<Course> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<Course> courses = null;

    public CourseAdapter(Context context, int layoutResourceId, ArrayList<Course> courses){
        super(context,layoutResourceId,courses);
        this.layoutResourceId=layoutResourceId;
        this.context=context;
        this.courses=courses;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        View row=convertView;
        CourseHolder holder;

        if(row==null){
            LayoutInflater inflater=(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=inflater.inflate(layoutResourceId, parent, false);

            holder=new CourseHolder();
            holder.courseSymbol = (ImageView) row.findViewById(R.id.coursePicture);
            holder.courseName = (TextView) row.findViewById(R.id.courseName);
            row.setTag(holder);
        }
        else{
            holder=(CourseHolder) row.getTag();
        }

        Course course = courses.get(position);
        holder.courseName.setText(course.getName());
        holder.courseSymbol.setImageResource(course.getPictureId());

        return row;
    }


    static private class CourseHolder{
        ImageView courseSymbol;
        TextView courseName;
    }

    public Course getItemAtPosition(int position){
        return courses.get(position);
    }

}
