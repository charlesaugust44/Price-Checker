package com.charlesaugust44.pricechecker.model;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Invoice {
    private String company, key, date, address, district, city;
    private double total;
    private List<Product> products;

    public Invoice(JSONObject jsondata) throws JSONException {
        this.key = jsondata.getString("key");
        this.company = jsondata.getString("company");
        this.total = jsondata.getDouble("total");
        this.date = jsondata.getString("date");
        this.address = jsondata.getString("address");
        this.district = jsondata.getString("district");
        this.city = jsondata.getString("city");

        if(jsondata.has("products")) {
            JSONArray prods = jsondata.getJSONArray("products");
            products = new ArrayList<>();

            for (int i = 0; i < prods.length(); i++) {
                Product p = new Product(prods.getJSONObject(i));
                p.setInvoice(this);
                products.add(p);
            }
        }
    }

    public Invoice(String json) throws JSONException {
        this(new JSONObject(json));
    }

    public JSONObject getJson() {
        JSONObject output = new JSONObject();

        try {
            output.put("key", key);
            output.put("company", company);
            output.put("total", total);
            output.put("date", date);
            output.put("address", address);
            output.put("district", district);
            output.put("city", city);
            output.put("products", products);
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
