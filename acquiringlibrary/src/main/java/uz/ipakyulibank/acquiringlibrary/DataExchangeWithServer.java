package uz.ipakyulibank.acquiringlibrary;

import java.util.HashMap;

interface DataExchangeWithServer {
    void sendDataToServer(AsyncResponse delegate, String sURL, HashMap<String, String> params);
}
