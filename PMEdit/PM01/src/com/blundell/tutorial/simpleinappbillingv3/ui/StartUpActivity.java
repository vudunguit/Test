package com.blundell.tutorial.simpleinappbillingv3.ui;

import android.content.Intent;
import android.os.Bundle;

import com.android.vending.billing.util.IabHelper.OnIabSetupFinishedListener;
import com.android.vending.billing.util.IabResult;
import com.blundell.tutorial.simpleinappbillingv3.ui.base.PurchaseActivity;
import com.blundell.tutorial.simpleinappbillingv3.util.Log;

/**
 * Checks that In App Purchasing is available on this device
 * 
 * @author Blundell
 * 
 */
public class StartUpActivity extends PurchaseActivity implements OnIabSetupFinishedListener {
	public static final int REQUEST_GOLD_PURCHASE = 100;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("App started");
    }

    @Override
    public void onIabSetupFinished(IabResult result) {
        if (result.isSuccess()) {
            Log.d("In-app Billing set up" + result);
            dealWithIabSetupSuccess();
        } else {
            Log.d("Problem setting up In-app Billing: " + result);
            dealWithIabSetupFailure();
        }
    }

    @Override
    protected void dealWithIabSetupFailure() {
        popBurntToast("Sorry In App Billing isn't available on your device");
    }
    
    @Override
    protected void dealWithIabSetupSuccess() {
    	navigate().toMainActivity();
    	finish();
    }
    

/*    @Override
    protected void dealWithPurchaseSuccess(IabResult result, Purchase info) {
        super.dealWithPurchaseSuccess(result, info);
        setResult(RESULT_OK);
//        finish();
    }

    @Override
    protected void dealWithPurchaseFailed(IabResult result) {
        super.dealWithPurchaseFailed(result);
        setResult(RESULT_CANCELED);
//        finish();
    }*/
    
}
