package com.charlesaugust44.pricechecker.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.charlesaugust44.pricechecker.R;

import java.util.List;

public class InvoiceAdapter extends ArrayAdapter<Invoice> {

    private final Context context;
    private final List<Invoice> invoices;

    public InvoiceAdapter(@NonNull Context context, int resource, @NonNull List<Invoice> objects) {
        super(context, resource, objects);
        this.context = context;
        this.invoices = objects;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.invoice_item, parent, false);

        TextView textCompanyName = rowView.findViewById(R.id.text_companyName);
        TextView textTotal = rowView.findViewById(R.id.text_invoiceTotal);
        TextView textItems = rowView.findViewById(R.id.text_invoiceItems);
        TextView textDate = rowView.findViewById(R.id.text_invoiceDate);

        Invoice p = this.invoices.get(position);

        textCompanyName.setText(p.getCompany());
        textTotal.setText("R$ " + p.getTotal());
        textItems.setText(p.getProducts().size() + " Items");
        textDate.setText(p.getDate());

        return rowView;
    }
}
