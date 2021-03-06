package com.example.androidapps;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.collection.ArraySet;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShoppingListActivity extends AppCompatActivity {

    @BindView(R.id.itemName_ET)
    EditText itemName;
    @BindView(R.id.itemList)
    ListView itemList;
    @BindView(R.id.itemSpinner)
    Spinner itemSpinner;

    private List<String> listItems;
    private List<String> spinnerItems;
    private Set<String> listSetItems;
    private Set<String> spinnerSetItems;

    private static final String LIST_ITEMS_KEY = "LIST_ITEMS_KEY";
    private static final String LIST_SPINNER_KEY = "LIST_SPINNER_KEY";
    private static final String SHOPPING_LIST_KEY = "SHOPPING_LIST_KEY";

    private ShoppingListAdapter<String> listAdapter;
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shopping_list);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemName.getText() != null && !itemName.getText().toString().trim().isEmpty() &&
                listSetItems.add(itemName.getText().toString())) {
                    listItems.add(itemName.getText().toString());
                    itemName.setText("");
                    listAdapter.notifyDataSetChanged();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sp = getSharedPreferences(SHOPPING_LIST_KEY, MODE_PRIVATE);
        listSetItems  = sp.getStringSet(LIST_ITEMS_KEY, new ArraySet<>());
        spinnerSetItems  = sp.getStringSet(LIST_SPINNER_KEY, new ArraySet<>());
        listItems = new ArrayList<>(listSetItems);
        spinnerItems = new ArrayList<>(spinnerSetItems);
        spinnerAdapter =
                new ArrayAdapter<>(this, android.R.layout.select_dialog_item, spinnerItems);
        itemSpinner.setAdapter(spinnerAdapter);
        itemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!spinnerItems.get(i).equals("")) {
                    itemName.setText("" + spinnerItems.get(i));
                    spinnerItems.remove(i);
                    spinnerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        listAdapter = new ShoppingListAdapter<>(this, R.layout.row_shopping_list, listItems,
                spinnerAdapter, spinnerItems, itemSpinner);
        itemList.setAdapter(listAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = getSharedPreferences(SHOPPING_LIST_KEY, MODE_PRIVATE).edit();
        listSetItems = new ArraySet<>(listItems);
        spinnerSetItems = new ArraySet<>(spinnerItems);
        editor.putStringSet(LIST_ITEMS_KEY, listSetItems);
        editor.putStringSet(LIST_SPINNER_KEY, spinnerSetItems);
        editor.commit(); // UWAGA Może przywiesić wątek, można użyć .apply() działa w innym wątku, a nie chcemy odczytania przed zapisem
    }
}
