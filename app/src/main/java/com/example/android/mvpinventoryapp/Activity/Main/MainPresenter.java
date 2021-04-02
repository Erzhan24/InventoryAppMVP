package com.example.android.mvpinventoryapp.Activity.Main;

import android.app.Application;
import com.example.android.mvpinventoryapp.Model.Inventory;
import com.example.android.mvpinventoryapp.Repository.InventoryRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MainPresenter extends AndroidViewModel implements MainContract.Presenter {

    private InventoryRepository repository;
    private LiveData<List<Inventory>> allInventories;

    public MainPresenter(@NonNull Application application) {
        super(application);
        repository = new InventoryRepository(application);
        allInventories = repository.getAllInventories();
    }

    public LiveData<List<Inventory>> getAllInventories() {
        return allInventories;
    }


    @Override
    public void insert(Inventory inventory) {
        repository.insert(inventory);
    }

    @Override
    public void update(Inventory inventory) {
        repository.update(inventory);
    }

    @Override
    public void delete(Inventory inventory) {
        repository.delete(inventory);
    }

    @Override
    public void deleteAllInventories() {
        repository.deleteAllInventories();
    }
}
