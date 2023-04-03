package com.charlesaugust44.pricechecker.model;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class Product {
    private String name, code, barcode, unit;
    private double quantity, unitValue, value;
    private Invoice invoice;

    public Product(JSONObject jsondata) throws JSONException {
        this.name = jsondata.getString("name");
        this.code = jsondata.getString("code");
        this.barcode = jsondata.getString("barcode");
        this.unit = jsondata.getString("unit");
        this.quantity = jsondata.getDouble("quantity");
        this.unitValue = jsondata.getDouble("unitValue");
        this.value = jsondata.getDouble("value");

        if (jsondata.has("invoice")) {
            this.invoice = new Invoice(jsondata.getJSONObject("invoice"));
        }
    }

    public Product(String json) throws JSONException {
        this(new JSONObject(json));
    }

    public JSONObject getJson() {
        JSONObject output = new JSONObject();

        try {
            output.put("name", name);
            output.put("code", code);
            output.put("barcode", barcode);
            output.put("unit", unit);
            output.put("quantity", quantity);
            output.put("unitValue", unitValue);
            output.put("value", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return output;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getJson().toString();
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getUnitValue() {
        return unitValue;
    }

    public void setUnitValue(double unitValue) {
        this.unitValue = unitValue;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
