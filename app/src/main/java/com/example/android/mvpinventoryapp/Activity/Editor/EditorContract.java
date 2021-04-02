package com.example.android.mvpinventoryapp.Activity.Editor;

import android.content.DialogInterface;
import  com.example.android.mvpinventoryapp.Model.Inventory;

public interface EditorContract {
    interface Presenter {
        void update(Inventory inventory);

    }

    interface View {
        void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener);
        void saveInventory();
        void dispatchTakePictureIntent(android.view.View view);
    }
}

