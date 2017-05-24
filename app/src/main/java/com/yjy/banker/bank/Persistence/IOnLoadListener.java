package com.yjy.banker.bank.Persistence;

public interface IOnLoadListener {
    boolean onLoad(long id, long balance, String name, String description, String photoUUID);
}
