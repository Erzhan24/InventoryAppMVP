package com.example.android.mvpinventoryapp.Activity.Main;

import com.example.android.mvpinventoryapp.Model.Inventory;

public interface MainContract {
    interface Presenter {
        void insert(Inventory inventory);
        void update(Inventory inventory);
        void delete(Inventory inventory);
        void deleteAllInventories();
    }

    interface View {

    }
}
