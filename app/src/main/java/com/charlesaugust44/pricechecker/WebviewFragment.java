package com.charlesaugust44.pricechecker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.charlesaugust44.pricechecker.WebviewFragmentDirections.ActionNavWebviewToNavInvoiceList;

public class WebviewFragment extends Fragment {
    public static final String ARG_PARAM1 = "key";

    WebView webview;
    FloatingActionButton fabImport;
    String html, key;

    public WebviewFragment() {
        // Required empty public constructor
    }

    public static WebviewFragment newInstance(String key) {
        WebviewFragment fragment = new WebviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        key = args.getString(ARG_PARAM1, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);

        webview = view.findViewById(R.id.webview);
        fabImport = view.findViewById(R.id.fab_import);
        WebView.setWebContentsDebuggingEnabled(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webview.evaluateJavascript(
                        String.format("(function() { document.getElementById('formPrincipal:chaveacesso').value = '%s'; })();", key),
                        null);
            }
        });
        webview.loadUrl("https://portalsped.fazenda.mg.gov.br/portalnfce/sistema/consultaarg.xhtml");

        fabImport.setOnClickListener(v -> {
            webview.evaluateJavascript(
                    "(function() {\n" +
                            "let productContainers = $('#accordion1 > .panel');\n" +
                            "let replaceAll = function (needle, replace, haystack) {\n" +
                            "\treturn haystack.split(needle).map(word => word.trim()).join(replace);\n" +
                            "};\n" +
                            "let products = [];\n" +
                            "\n" +
                            "for(let i = 0; i < productContainers.length; i++) {\n" +
                            "    let rows = $(productContainers[i]).find('.panel-body:first .table');\n" +
                            "  \n" +
                            "    let value = $(productContainers[i]).find('.panel-heading:first .row div:nth-child(6)').first().text().replace('R$','').replace(' ','').replace(',','.');\n" +
                            "    let unitValue = $($(rows).get(7)).find('td:nth-child(1)').first().text().replace('R$','').replace(' ','').replace(',','.');\n" +
                            "    let quantity = $($(rows).get(5)).find('td:nth-child(3)').first().text();\n" +
                            "    \n" +
                            "    value = replaceAll('\\n', '', replaceAll('\\t', '', value));\n" +
                            "    \n" +
                            "    let product = {\n" +
                            "\tname: $(productContainers[i]).find('.panel-heading:first .row div:nth-child(3)').first().text(),\n" +
                            "\tcode: $($(rows).get(0)).find('td:nth-child(1)').first().text(),\n" +
                            "\tbarcode: $($(rows).get(5)).find('td:nth-child(1)').first().text(),\n" +
                            "\tunit: $($(rows).get(5)).find('td:nth-child(2)').first().text(),\n" +
                            "\tquantity: parseFloat(quantity),\t\n" +
                            "\tunitValue: parseFloat(unitValue),\n" +
                            "\tvalue: parseFloat(value),\n" +
                            "    };\n" +
                            "    \n" +
                            "    product.name = replaceAll('\\n', '', replaceAll('\\t', '', product.name));\n" +
                            "    products.push(product);\n" +
                            "}\n" +
                            "\n" +
                            "let detailContainers = $('.ui-tabs-panels > .ui-tabs-panel:first-child table');\n" +
                            "let issuerContainers = $('.ui-tabs-panels > .ui-tabs-panel:nth-child(2) table').get(2);\n" +
                            "\n" +
                            "let nfc = {\n" +
                            "\tkey: $('h5').first().text(),\n" +
                            "\tcompany: $(detailContainers.get(2)).find('td:nth-child(2)').first().text(),\n" +
                            "\tdate: $(detailContainers.get(0)).find('td:nth-child(4)').first().text(),\n" +
                            "\ttotal: parseFloat($(detailContainers.get(1)).find('td:nth-child(1)').first().text().replace('R$','').replace(' ','').replace(',','.')),\n" +
                            "\taddress: $(issuerContainers).find('td:nth-child(1)').first().text(),\n" +
                            "\tdistrict: $(issuerContainers).find('td:nth-child(2)').first().text(),\n" +
                            "\tcity: $(issuerContainers).find('td:nth-child(3)').first().text(),\n" +
                            "\tproducts\n" +
                            "};\n" +
                            "nfc.key = replaceAll('-','',replaceAll('/','',replaceAll('.','',replaceAll('\\n', '', replaceAll('\\t', '', nfc.key)))));\n" +
                            "return replaceAll('\"', '\\'', JSON.stringify(nfc));\n" +
                            "})();",
                    json -> {
                        json = json.substring(1, json.length() - 1).replaceAll("'", "\"");

                        ActionNavWebviewToNavInvoiceList action = WebviewFragmentDirections.actionNavWebviewToNavInvoiceList(json);
                        NavController nav = Navigation.findNavController(view);
                        nav.navigate(action);
                    });
        });

        return view;
    }
}