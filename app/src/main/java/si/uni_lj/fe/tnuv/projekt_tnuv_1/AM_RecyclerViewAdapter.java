package si.uni_lj.fe.tnuv.projekt_tnuv_1;


import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

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
        holder.assetValueEditText.setText(String.valueOf(assetModels.get(position).getCurrentValue()));
    }

    @Override
    public int getItemCount() {
        return assetModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        boolean isFirstPopulation;
        TextView tvAssetName, tvTargetValue;
        TextView assetValueEditText;

        ViewHolder(View itemView) {
            super(itemView);
            tvAssetName = itemView.findViewById(R.id.tvAsset);
            tvTargetValue = itemView.findViewById(R.id.tvTargetValue);
            assetValueEditText = itemView.findViewById(R.id.etCurrentValue);
            assetValueEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    // Inflate the custom layout
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View dialogView = inflater.inflate(R.layout.current_value_dialog, null);
                    // Set the custom layout to the AlertDialog builder
                    builder.setView(dialogView);
                    // Get the EditText and ImageViews from the custom layout
                    EditText input = dialogView.findViewById(R.id.dialog_edit_text);
                    ImageView crossImage = dialogView.findViewById(R.id.cross_image);
                    ImageView arrowImage = dialogView.findViewById(R.id.arrow_image);

                    // Create and show the AlertDialog
                    AlertDialog dialog = builder.create();
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.rounded_dialog);
                    // Set an OnClickListener on the cross ImageView
                    crossImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Dismiss the dialog
                            dialog.dismiss();
                        }
                    });
                    // Set an OnClickListener on the arrow ImageView
                    arrowImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = getBindingAdapterPosition();
                            AssetModel assetModel = assetModels.get(position);
                            String assetName = assetModel.getAssetName();
                            int newValue;
                            if (input.getText().toString().isEmpty()) {
                                newValue = 0;
                            } else {
                                newValue = Integer.parseInt(input.getText().toString());
                            }
                            assetModel.setCurrentValue(newValue);
                            userPortfolio.put(assetName, newValue);
                            assetValueEditText.setText(String.valueOf(newValue));
                            // Update all the values in the Firestore
                            updateFirestore(userPortfolio);
                            // Dismiss the dialog
                            dialog.dismiss();
                        }
                    });
                    input.setText(assetValueEditText.getText());
                    // Request focus on the EditText and show the keyboard
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    input.requestFocus();
                    input.post(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });
                    dialog.show();
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
//                        Toast.makeText(context, "Portfolio updated", Toast.LENGTH_SHORT).show();
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