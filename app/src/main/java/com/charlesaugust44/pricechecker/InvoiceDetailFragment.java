package com.charlesaugust44.pricechecker;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.charlesaugust44.pricechecker.model.Invoice;
import com.charlesaugust44.pricechecker.model.Product;
import com.charlesaugust44.pricechecker.model.ProductAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InvoiceDetailFragment extends Fragment {
    public static final String ARG_PARAM1 = "key";

    private String key;

    TextView textCompany, textKey, textAddress, textDate, textTotal;
    ListView listProducts;
    ProductAdapter productAdapter;
    ProgressDialog progressDialog;
    List<Product> products;

    public InvoiceDetailFragment() {
    }

    public static InvoiceDetailFragment newInstance(String key) {
        InvoiceDetailFragment fragment = new InvoiceDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            key = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invoice_detail, container, false);

        progressDialog = new ProgressDialog(view.getContext());

        textCompany = view.findViewById(R.id.text_company);
        textKey = view.findViewById(R.id.text_key);
        textAddress = view.findViewById(R.id.text_address);
        textDate = view.findViewById(R.id.text_date);
        textTotal = view.findViewById(R.id.text_total);
        listProducts = view.findViewById(R.id.list_products);

        products = new ArrayList<>();
        productAdapter = new ProductAdapter(view.getContext(), R.id.list_products, products);
        listProducts.setAdapter(productAdapter);

        InvoiceDetailGetTask task = new InvoiceDetailGetTask();
        task.execute();

        return view;
    }

    public class InvoiceDetailGetTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.dismiss();
            progressDialog.setMessage("Loading Invoice");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            return Api.get(Api.INVOICE_ENDPOINT + "/" + key, "");
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();

            try {
                Invoice invoice = new Invoice(s);

                textCompany.setText(invoice.getCompany());
                textKey.setText(invoice.getKey());
                textAddress.setText(invoice.getAddress() + ", " + invoice.getDistrict() + " - " + invoice.getCity());
                textDate.setText(invoice.getDate());
                textTotal.setText("R$ " + invoice.getTotal());

                products.clear();
                products.addAll(invoice.getProducts());
                productAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}