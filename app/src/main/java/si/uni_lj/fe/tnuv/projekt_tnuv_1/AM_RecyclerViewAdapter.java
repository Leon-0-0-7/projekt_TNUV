package si.uni_lj.fe.tnuv.projekt_tnuv_1;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AM_RecyclerViewAdapter extends RecyclerView.Adapter<AM_RecyclerViewAdapter.ViewHolder>{
    final Context context;
    ArrayList<AssetModel> assetModels;
    Map<String, Integer> userPortfolio;

    public AM_RecyclerViewAdapter(Context context, ArrayList<AssetModel> assetModels, Map<String, Integer> userPortfolio) {
        this.context = context;
        this.assetModels = assetModels;
        this.userPortfolio = userPortfolio;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.asset_item_recycler_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // If the view holder is being populated for the first time
        holder.isFirstPopulation = true;
        holder.tvAssetName.setText(assetModels.get(position).getAssetName());
        holder.tvTargetValue.setText(String.valueOf(assetModels.get(position).getTargetValue()));
        holder.etCurrentValue.setText(String.valueOf(assetModels.get(position).getCurrentValue()));
    }

    @Override
    public int getItemCount() {
        return assetModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        boolean isFirstPopulation;
        TextView tvAssetName, tvTargetValue;
        EditText etCurrentValue, assetValueEditText;

        ViewHolder(View itemView) {
            super(itemView);
            tvAssetName = itemView.findViewById(R.id.tvAsset);
            tvTargetValue = itemView.findViewById(R.id.tvTargetValue);
            etCurrentValue = itemView.findViewById(R.id.etCurrentValue);
            assetValueEditText = itemView.findViewById(R.id.etCurrentValue);

            assetValueEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    int position = getAdapterPosition();
                    AssetModel assetModel = assetModels.get(position);
                    String assetName = assetModel.getAssetName();
                    int newValue;
                    if (assetValueEditText.getText().toString().isEmpty()) {
                        newValue = 0;
                    } else {
                        newValue = Integer.parseInt(assetValueEditText.getText().toString());
                    }
                    assetModel.setCurrentValue(newValue);
                    userPortfolio.put(assetName, newValue);
                    // Update all the values in the Firestore
                    updateFirestore(userPortfolio);
                }
            });
        }

        private void updateFirestore(Map<String, Integer> userPortfolio) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            Map<String, Object> data = new HashMap<>();
            data.put("portfolio", userPortfolio);

            db.collection("users").document(userId).update(data).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (isFirstPopulation) {
                        Toast.makeText(context, "Portfolio updated", Toast.LENGTH_SHORT).show();
                        isFirstPopulation = false;
                    }
                } else {
                    // Handle errors
                    Toast.makeText(context, "Failed to update portfolio", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}