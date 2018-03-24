package com.kimmj.ardapp;

import android.content.Context;

import org.ethereum.geth.Account;
import org.ethereum.geth.Accounts;
import org.ethereum.geth.BigInt;
import org.ethereum.geth.Geth;
import org.ethereum.geth.KeyStore;
import org.ethereum.geth.Transaction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * class DMapAccount
 * store the user's account information
 */
public class DMapAccount {
    private String keyPath;
    private KeyStore ks;

    DMapAccount(String path) {
        keyPath = path;
        File keyStoreFile = new File(path);
        // make a new keystore if there isn't existing one
        if(!keyStoreFile.exists()) {
            keyStoreFile.mkdir();
        }

        ks = new KeyStore(path, Geth.LightScryptN, Geth.LightScryptP);
    }

    public Account MakeAccount(String createPass) throws Exception {
        Account newAcc = ks.newAccount(createPass);
        return newAcc;
        // only when the user wanted to export a key file
        //byte[] jsonAcc = ks.exportKey(newAcc, createPass, exportPass);
        //ks.updateAccount(newAcc, createPass, exportPass);
    }

    public void UpdateAccount(Account acc, String createPass, String updatePass) throws Exception {
        ks.updateAccount(acc, createPass, updatePass);
    }

    public void deleteAccount(Account acc, String createPass) throws Exception {
        ks.deleteAccount(acc, createPass);
    }

    public boolean existAccount(Account acc) {
        if (acc == null) return false;
        else return ks.hasAddress(acc.getAddress());
    }

    public List<Account> getAccountList() throws Exception {
        List<Account> accList = new ArrayList<>();
        Accounts accounts = ks.getAccounts();
        for (int i = 0; i < accounts.size(); i++) {
            accList.add(accounts.get(i));
        }

        return accList;
    }

    public KeyStore getkeyStore() {
        return ks;
    }

    public void SignTransaction(Account signer, Transaction tx, String pass) throws Exception {
        //Account signer = ks.newAccount(pass);
        BigInt chain = new BigInt(1); // Chain identifier of the main net
        Transaction signed = ks.signTxPassphrase(signer, pass, tx, chain);

        // Sign a transaction with multiple manually cancelled authorizations
        ks.unlock(signer, pass);
        signed = ks.signTx(signer, tx, chain);
        ks.lock(signer.getAddress());
    }
}
