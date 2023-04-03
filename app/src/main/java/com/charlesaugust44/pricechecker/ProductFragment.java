package com.charlesaugust44.pricechecker;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.charlesaugust44.pricechecker.model.Product;
import com.charlesaugust44.pricechecker.model.ProductAdapter;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class ProductFragment extends Fragment {
    Button btnScan;
    EditText fieldCodebar;
    ListView listProducts;
    List<Product> products;
    ProductAdapter productAdapter;
    ProgressDialog progressDialog;

    public ProductFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment ProductFragment.
     */
    public static ProductFragment newInstance() {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        btnScan = view.findViewById(R.id.btn_scan);
        fieldCodebar = view.findViewById(R.id.field_codebar);
        listProducts = view.findViewById(R.id.list_products);

        btnScan.setOnClickListener(v -> {
            scanCode();
        });

        products = new ArrayList<>();

        this.getProducts("");
        productAdapter = new ProductAdapter(view.getContext(), R.layout.product_item, products);

        listProducts.setAdapter(productAdapter);

        return view;
    }

    private void getProducts(String search) {
        ProductListTask task = new ProductListTask();
        task.execute(search);
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();

        options.setPrompt("Volume Up to flash on");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(Capture.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            fieldCodebar.setText(result.getContents());
            getProducts(result.getContents());
        }
    });

    public class ProductListTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ProductFragment.this.getContext());
            progressDialog.setMessage("Loading results");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String param = strings[0];

            if (!param.equals("")) {
                param = "?search=" + param;
            }

            return Api.get(Api.PRODUCT_ENDPOINT, param);
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            products.clear();

            try {
                JSONArray jsonProducts = new JSONArray(s);

                for (int i = 0; i < jsonProducts.length(); i++) {
                    products.add(new Product(jsonProducts.getJSONObject(i)));
                }
                productAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}