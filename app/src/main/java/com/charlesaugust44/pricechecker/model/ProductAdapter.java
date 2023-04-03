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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends ArrayAdapter<Product> {

    private final Context context;
    private final List<Product> products;

    public ProductAdapter(@NonNull Context context, int resource, @NonNull List<Product> objects) {
        super(context, resource, objects);
        this.context = context;
        this.products = objects;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.product_item, parent, false);

        TextView textName = rowView.findViewById(R.id.text_name);
        TextView textPrice = rowView.findViewById(R.id.text_price);
        TextView textCodebar = rowView.findViewById(R.id.text_codebar);
        TextView textDate = rowView.findViewById(R.id.text_date);
        TextView textUnity = rowView.findViewById(R.id.text_unity);

        Product p = this.products.get(position);

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;

        try {
            date = inputFormat.parse(p.getInvoice().getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        textName.setText(p.getName());
        textPrice.setText("R$ " + p.getUnitValue());
        textCodebar.setText(p.getBarcode());
        textDate.setText(outputFormat.format(date));
        textUnity.setText(p.getUnit().toUpperCase());

        return rowView;
    }
}
