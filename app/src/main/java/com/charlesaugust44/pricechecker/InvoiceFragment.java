package com.charlesaugust44.pricechecker;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.charlesaugust44.pricechecker.InvoiceFragmentDirections.*;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.charlesaugust44.pricechecker.model.Invoice;
import com.charlesaugust44.pricechecker.model.InvoiceAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.redmadrobot.inputmask.MaskedTextChangedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InvoiceFragment extends Fragment implements AdapterView.OnItemClickListener {
    public static final String ARG_PARAM1 = "json";

    FloatingActionButton fabScan;
    String invoiceUrl;
    EditText fieldInvoiceKey;
    ListView listInvoices;
    Button btnAddInvoice;
    List<Invoice> invoices;
    InvoiceAdapter invoiceAdapter;
    ProgressDialog progressDialog;
    String json;
    View view;

    static String TAG = "INVOICE_FRAGMENT";

    public InvoiceFragment() {
        json = "";
    }

    public static InvoiceFragment newInstance(String json) {
        InvoiceFragment fragment = new InvoiceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, json);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        if (args == null) return;

        json = args.getString(ARG_PARAM1, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_invoice, container, false);

        fabScan = view.findViewById(R.id.fab_scan);
        listInvoices = view.findViewById(R.id.list_invoices);
        fieldInvoiceKey = view.findViewById(R.id.field_invoiceKey);
        btnAddInvoice = view.findViewById(R.id.btn_addInvoice);
        progressDialog = new ProgressDialog(this.getContext());

        final MaskedTextChangedListener listener = MaskedTextChangedListener.Companion.installOn(
                fieldInvoiceKey,
                "[0000] [0000] [0000] [0000] [0000] [0000] [0000] [0000] [0000] [0000] [0000]",
                null
        );

        fieldInvoiceKey.setHint(listener.placeholder());

        btnAddInvoice.setOnClickListener(v -> {
            openWebview(fieldInvoiceKey.getText().toString().replaceAll(" ", ""));
        });

        fabScan.setOnClickListener(v -> {
            scanCode();
        });

        Log.d(TAG, "onCreateView: " + json);

        if (!Objects.equals(json, "")) {
            postInvoice();
        }

        invoices = new ArrayList<>();

        getInvoices();
        invoiceAdapter = new InvoiceAdapter(view.getContext(), R.layout.invoice_item, invoices);
        listInvoices.setAdapter(invoiceAdapter);
        listInvoices.setOnItemClickListener(this);

        return view;
    }

    private void getInvoices() {
        InvoiceListTask task = new InvoiceListTask();
        task.execute();
    }

    private void postInvoice() {
        InvoicePostTask task = new InvoicePostTask();
        task.execute(json);
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();

        options.setPrompt("Volume Up to flash on");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(Capture.class);
        barLauncher.launch(options);
    }

    private void openWebview(String key) {
        ActionNavInvoiceListToNavWebview action = InvoiceFragmentDirections.actionNavInvoiceListToNavWebview(key);
        Navigation.findNavController(view).navigate(action);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            invoiceUrl = result.getContents();

            if (!invoiceUrl.matches("http.+\\?p=\\d{44}|.+")) {
                Toast.makeText(InvoiceFragment.this.getContext(), "The QR is invalid: \"" + invoiceUrl + "\"", Toast.LENGTH_LONG).show();
                return;
            }

            String key = invoiceUrl
                    .split("\\?")[1]
                    .split("=")[1]
                    .split("\\|")[0];

            openWebview(key);
        }
    });

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ActionNavInvoiceListToNavInvoiceDetail action = InvoiceFragmentDirections.actionNavInvoiceListToNavInvoiceDetail(invoices.get(position).getKey());
        Navigation.findNavController(view).navigate(action);
    }

    public class InvoiceListTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.dismiss();
            progressDialog.setMessage("Loading list");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            return Api.get(Api.INVOICE_ENDPOINT, "");
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            invoices.clear();

            try {
                JSONArray jsonInvoices = new JSONArray(s);

                for (int i = 0; i < jsonInvoices.length(); i++) {
                    invoices.add(new Invoice(jsonInvoices.getJSONObject(i)));
                }
                invoiceAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class InvoicePostTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.dismiss();
            progressDialog.setMessage("Saving invoice");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            return Api.post(Api.INVOICE_ENDPOINT, strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            String message = "";

            switch (s) {
                case "409":
                    message = "Invoice already exists!";
                    break;
                case "201":
                    message = "Invoice Saved";
                    break;
                default:
                    message = "WARNING: " + s;
            }

            Toast.makeText(InvoiceFragment.this.getContext(), message, Toast.LENGTH_LONG).show();
            InvoiceListTask task = new InvoiceListTask();
            task.execute();
        }
    }
}