package si.uni_lj.fe.tnuv.projekt_tnuv_1;

public class AssetModel {
    String AssetName;
    int targetValue;
    int currentValue;

    public AssetModel(String assetName, int targetValue, int currentValue) {
        AssetName = assetName;
        this.targetValue = targetValue;
        this.currentValue = currentValue;
    }
    public String getAssetName() {
        return AssetName;
    }

    public void setAssetName(String assetName) {
        AssetName = assetName;
    }

    public int getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(int targetValue) {
        this.targetValue = targetValue;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }


}
