package com.example.android.mvpinventoryapp.Activity.Main;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.Observer;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import com.example.android.mvpinventoryapp.Adapter.InventoryAdapter;
import com.example.android.mvpinventoryapp.Model.Inventory;
import com.example.android.mvpinventoryapp.R;
import com.example.android.mvpinventoryapp.Activity.Editor.EditorActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_INVENTORY = 1;
    public static final int EDIT_INVENTORY = 2;
    private MainPresenter catalogPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton button = findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivityForResult(intent, ADD_INVENTORY);
            }
        });

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final InventoryAdapter adapter = new InventoryAdapter();
        recyclerView.setAdapter(adapter);

        catalogPresenter = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(this.getApplication()))
                .get(MainPresenter.class);
        catalogPresenter.getAllInventories().observe(this, new Observer<List<Inventory>>() {
            @Override
            public void onChanged(List<Inventory> inventories) {
                adapter.setInventories(inventories);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogStyle);
                builder.setTitle(R.string.delete_dialog_msg);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        catalogPresenter.delete(adapter.inventoryPosition(viewHolder.getAdapterPosition()));
                        Toast.makeText(MainActivity.this, R.string.editor_delete_inventory_successful, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        Toast.makeText(getApplicationContext(),
                                R.string.notDeleted, Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new InventoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final Inventory inventory) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                intent.putExtra(EditorActivity.EXTRA_ID, inventory.getId());
                intent.putExtra(EditorActivity.EXTRA_NAME, inventory.getTitle());
                intent.putExtra(EditorActivity.EXTRA_MODEL, inventory.getModel());
                intent.putExtra(EditorActivity.EXTRA_PRICE, inventory.getPrice());
                intent.putExtra(EditorActivity.EXTRA_QUANTITY, inventory.getQuantity());
                intent.putExtra(EditorActivity.EXTRA_SUPPLIER, inventory.getSupplier());
                intent.putExtra(EditorActivity.EXTRA_IMAGE, inventory.getImg());
                startActivityForResult(intent, 2);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String title = data.getStringExtra(EditorActivity.EXTRA_NAME);
            String model = data.getStringExtra(EditorActivity.EXTRA_MODEL);
            int price = data.getIntExtra(EditorActivity.EXTRA_PRICE, 1);
            int quantity = data.getIntExtra(EditorActivity.EXTRA_QUANTITY, 1);
            String suppl = data.getStringExtra(EditorActivity.EXTRA_SUPPLIER);
            String image = data.getStringExtra(EditorActivity.EXTRA_IMAGE);

            Inventory inventory = new Inventory(title, model, price, quantity, suppl, image);
            catalogPresenter.insert(inventory);

            Toast.makeText(this, R.string.editor_insert_inventory_successful, Toast.LENGTH_SHORT).show();
        } else if (requestCode == 2 && resultCode == RESULT_OK) {

            int id = data.getIntExtra(EditorActivity.EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(this, R.string.editor_insert_inventory_failed, Toast.LENGTH_SHORT).show();
                return;
            }

            String title = data.getStringExtra(EditorActivity.EXTRA_NAME);
            String model = data.getStringExtra(EditorActivity.EXTRA_MODEL);
            int price = data.getIntExtra(EditorActivity.EXTRA_PRICE, 1);
            int quantity = data.getIntExtra(EditorActivity.EXTRA_QUANTITY, 1);
            String suppl = data.getStringExtra(EditorActivity.EXTRA_SUPPLIER);
            String images = data.getStringExtra(EditorActivity.EXTRA_IMAGE);
            Inventory inventory = new Inventory(title, model, price, quantity, suppl, images);
            inventory.setId(id);
            catalogPresenter.update(inventory);
            Toast.makeText(this, R.string.editor_update_inventory_successful, Toast.LENGTH_SHORT).show();


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all_entries:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogStyle);
                builder.setTitle(R.string.deleteAll);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        catalogPresenter.deleteAllInventories();
                        Toast.makeText(MainActivity.this, R.string.allProductsDeleted, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),
                                R.string.notDeleted, Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}