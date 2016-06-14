package com.example.moststarredrepositories;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * @author rachit
 */
public class ContributorAdapter extends RecyclerView.Adapter<ContributorAdapter.ViewHolder> {

    private Context mContext;
    private Contributor[] mDataSet;

    public ContributorAdapter(Contributor[] dataSet, Context context) {
        mDataSet = dataSet;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;
        private final TextView name;
        private final TextView link;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.person_name);
            link = (TextView) itemView.findViewById(R.id.person_link);
            image = (ImageView) itemView.findViewById(R.id.person_photo);
        }

        public ImageView getImage() {
            return image;
        }

        public TextView getName() {
            return name;
        }

        public TextView getLink() {
            return link;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_contributor, parent, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    // Get element from the dataset at this position and replace the contents of the view
    // with that element
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.getName().setText(mDataSet[position].getName());
        Picasso.with(mContext).load(mDataSet[position].getImageUrl()).into(holder.getImage());
        holder.getLink().setText(mDataSet[position].getRepoUrl());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
}
