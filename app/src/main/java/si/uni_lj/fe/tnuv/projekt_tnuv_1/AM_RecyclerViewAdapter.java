package si.uni_lj.fe.tnuv.projekt_tnuv_1;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AM_RecyclerViewAdapter extends RecyclerView.Adapter<AM_RecyclerViewAdapter.MyViewHolder>{
    Context context; //needed for inflating the layout
    ArrayList<AssetModel> assetModels; //data to be displayed
    public AM_RecyclerViewAdapter(Context context, ArrayList<AssetModel> assetModels) {
        this.context = context;
        this.assetModels = assetModels;
    }
    @NonNull
    @Override
    public AM_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for the item + give a look in our rows
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.asset_item_recycler_view, parent, false);
        return new AM_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AM_RecyclerViewAdapter.MyViewHolder holder, int position) {
        // Set the data in the item
        // based on postion of recycler view
        holder.tvAssetName.setText(assetModels.get(position).getAssetName());
        holder.tvTargetValue.setText(String.valueOf(assetModels.get(position).getTargetValue()));
        holder.etCurrentValue.setText(String.valueOf(assetModels.get(position).getCurrentValue()));
    }

    @Override
    public int getItemCount() {
        // Return the number of items in the list
        return assetModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        //  grabbing views FROM XML FILE and assigning them to variables

        TextView tvAssetName,tvTargetValue ;
        EditText etCurrentValue;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAssetName = itemView.findViewById(R.id.tvAsset);
            tvTargetValue = itemView.findViewById(R.id.tvTargetValue);
            etCurrentValue = itemView.findViewById(R.id.etCurrentValue);
        }
    }
}
