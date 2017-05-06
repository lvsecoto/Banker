package com.yjy.banker.bank.account;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ProfileTest {
    @Test
    public void setNameToAccountInfo() throws Exception {
        Profile info = new Profile();
        String name = "hello world";
        info.setName(name);
        assertEquals(name, info.getName());
    }

    @Test
    public void setDescriptionToAccountInfo() throws Exception {
        Profile info = new Profile();
        String description = "hello world again";
        info.setDescription(description);
        assertEquals(description, info.getDescription());
    }

    @Test
    public void setAvatarToAccountInfo() throws Exception {
        Profile info = new Profile();
        UUID avatar = UUID.randomUUID();
        info.setPhoto(avatar);
        assertEquals(avatar, info.getPhoto());
    }

    @Test
    public void compareBetweenToAccountInfo() throws Exception {
        Profile info1 = new Profile();
        Profile info2 = new Profile();

        String name = "hello world";
        String description = "hello world again";
        UUID avatar = UUID.randomUUID();

        info1.setName(name);
        info1.setDescription(description);
        info1.setPhoto(avatar);

        assertNotEquals(info1, info2);

        info2.setName(name);
        info2.setDescription(description);
        info2.setPhoto(avatar);

        assertEquals(info1, info2);

    }
}